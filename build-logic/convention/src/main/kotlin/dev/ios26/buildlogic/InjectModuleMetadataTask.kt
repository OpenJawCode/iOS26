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
                        zout.putNextEntry(ZipEntry(e.name))
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
    }
}
