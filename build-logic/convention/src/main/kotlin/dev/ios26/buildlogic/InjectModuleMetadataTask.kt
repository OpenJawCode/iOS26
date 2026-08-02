package dev.ios26.buildlogic

import org.gradle.api.DefaultTask
import org.gradle.api.file.ConfigurableFileCollection
import org.gradle.api.file.RegularFileProperty
import org.gradle.api.tasks.InputFiles
import org.gradle.api.tasks.OutputFile
import org.gradle.api.tasks.TaskAction
import java.io.File
import java.util.zip.ZipEntry
import java.util.zip.ZipInputStream
import java.util.zip.ZipOutputStream

abstract class InjectModuleMetadataTask : DefaultTask() {

    @get:InputFiles
    abstract val metadataFiles: ConfigurableFileCollection

    @get:OutputFile
    abstract val apkFile: RegularFileProperty

    @get:org.gradle.api.tasks.Input
    abstract val keystorePath: org.gradle.api.provider.Property<String>

    @get:org.gradle.api.tasks.Input
    abstract val apksignerPath: org.gradle.api.provider.Property<String>

    @get:org.gradle.api.tasks.Input
    abstract val zipalignPath: org.gradle.api.provider.Property<String>

    @TaskAction
    fun inject() {
        val apk = apkFile.get().asFile
        if (!apk.exists()) return
        val tmp = File(apk.parentFile, apk.name + ".tmp")
        ZipInputStream(apk.inputStream()).use { zin ->
            ZipOutputStream(tmp.outputStream()).use { zout ->
                var e = zin.nextEntry
                while (e != null) {
                    if (!e.name.startsWith("META-INF/xposed/")) {
                        val outEntry = ZipEntry(e.name)
                        if (e.name == "resources.arsc") {
                            outEntry.method = ZipEntry.STORED
                            outEntry.size = e.size
                            outEntry.compressedSize = e.size
                            outEntry.crc = e.crc
                        }
                        zout.putNextEntry(outEntry)
                        zin.copyTo(zout)
                        zout.closeEntry()
                    }
                    e = zin.nextEntry
                }
                metadataFiles.files.forEach { f ->
                    zout.putNextEntry(ZipEntry("META-INF/xposed/${f.name}"))
                    f.inputStream().copyTo(zout)
                    zout.closeEntry()
                }
            }
        }
        if (!tmp.renameTo(apk)) throw IllegalStateException("Failed to replace " + apk)
        align(apk)
        resign(apk)
    }

    private fun align(apk: java.io.File) {
        val aligned = java.io.File(apk.parentFile, apk.name + ".aligned")
        val process = ProcessBuilder(
            zipalignPath.get(), "-f", "4", apk.absolutePath, aligned.absolutePath,
        ).redirectErrorStream(true).start()
        val output = process.inputStream.bufferedReader().readText()
        if (process.waitFor() != 0) throw IllegalStateException("zipalign failed: $output")
        if (!aligned.renameTo(apk)) throw IllegalStateException("align replace failed")
    }

    private fun resign(apk: java.io.File) {
        val process = ProcessBuilder(
            apksignerPath.get(),
            "sign",
            "--ks", keystorePath.get(),
            "--ks-pass", "pass:android",
            "--ks-key-alias", "androiddebugkey",
            "--key-pass", "pass:android",
            apk.absolutePath,
        ).redirectErrorStream(true).start()
        val output = process.inputStream.bufferedReader().readText()
        if (process.waitFor() != 0) throw IllegalStateException("apksigner failed: $output")
    }
}
