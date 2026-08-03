#!/usr/bin/env python3
"""CC pixel forensics (project-ui-forensics skill): region sampling, glass checks,
readability deltas, scrim math. Usage: cc-pixels.py screenshot.png [--regions]"""

import collections
import sys

from PIL import Image

PANEL_X0 = 0.55  # panel occupies right 92% minus margin; sample inside it
PANEL_Y0 = 0.08
PANEL_Y1 = 0.7


def hist(img, x0, y0, x1, y1, step=5):
    c = collections.Counter()
    for x in range(int(x0), int(x1), step):
        for y in range(int(y0), int(y1), step):
            c[img.getpixel((x, y))] += 1
    return c.most_common(6)


def mean_luma(img, x0, y0, x1, y1, step=8):
    tot = n = 0
    for x in range(int(x0), int(x1), step):
        for y in range(int(y0), int(y1), step):
            r, g, b = img.getpixel((x, y))
            tot += 0.299 * r + 0.587 * g + 0.114 * b
            n += 1
    return tot / max(n, 1)


def main(path):
    img = Image.open(path).convert("RGB")
    w, h = img.size
    print(f"size {w}x{h}")
    print("panel-region dominant:", hist(img, w * PANEL_X0, h * PANEL_Y0, w * 0.99, h * PANEL_Y1))
    print("left-region dominant:", hist(img, w * 0.05, h * PANEL_Y0, w * 0.4, h * PANEL_Y1))
    print(f"panel mean luma {mean_luma(img, w * PANEL_X0, h * PANEL_Y0, w * 0.99, h * PANEL_Y1):.1f} "
          f"vs left {mean_luma(img, w * 0.05, h * PANEL_Y0, w * 0.4, h * PANEL_Y1):.1f} "
          f"(panel < left*1.15 => scrim+glass dimming evidence)")
    # accent tile check: active tiles get accent fill; look for saturated pixels inside panel
    sat = 0
    for x in range(int(w * PANEL_X0), w, 4):
        for y in range(int(h * PANEL_Y0), int(h * PANEL_Y1), 4):
            r, g, b = img.getpixel((x, y))
            mx, mn = max(r, g, b), min(r, g, b)
            if mx - mn > 60 and mx > 90:
                sat += 1
    print(f"saturated(accent-ish) px in panel: {sat}")


if __name__ == "__main__":
    main(sys.argv[1])
