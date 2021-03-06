require gtk+.inc

LIC_FILES_CHKSUM = "file://COPYING;md5=3bf50002aefd002f49e7bb854063f7e7 \
                    file://gtk/gtk.h;endline=27;md5=c59e0b4490dd135a5726ebf851f9b17f \
                    file://gdk/gdk.h;endline=27;md5=07db285ec208fb3e0bf7d861b0614202 \
                    file://tests/testgtk.c;endline=27;md5=262db5db5f776f9863e56df31423e24c"
SRC_URI = "http://download.gnome.org/sources/gtk+/2.24/gtk+-${PV}.tar.xz \
           file://xsettings.patch \
           file://run-iconcache.patch \
           file://configure-nm.patch \
           file://hardcoded_libtool.patch \
           file://cellrenderer-cairo.patch;striplevel=0 \
           file://toggle-font.diff;striplevel=0 \
           file://0001-bgo-584832-Duplicate-the-exec-string-returned-by-gtk.patch \
           file://doc-fixes.patch \
           file://0001-GtkButton-do-not-prelight-in-touchscreen-mode.patch \
	  "

# TO MERGE
#           file://entry-cairo.patch;striplevel=0
#           file://filesystem-volumes.patch
#           file://filechooser-props.patch
#           file://filechooser-default.patch
#           file://filechooser-sizefix.patch
# temporary
#           file://gtklabel-resize-patch
#           file://menu-deactivate.patch
#        file://combo-arrow-size.patch;striplevel=0
#            file://configurefix.patch

PR = "r0"

SRC_URI[md5sum] = "68f034fb4f68086790d7488cf00e71c8"
SRC_URI[sha256sum] = "d7e1c6d6ae6baa48c88441fe52b73b0fb6008763644867d59b049bafb0cf771b"

EXTRA_OECONF = "--without-libtiff --without-libjasper --enable-xkb --disable-glibtest --disable-cups --disable-xinerama"

LIBV = "2.10.0"

PACKAGES_DYNAMIC += "^gtk-immodule-.* ^gtk-printbackend-.*"
BBCLASSEXTEND = "native"
RRECOMMENDS_${PN}_class-native = ""
DEPENDS_class-native = "glib-2.0-native atk-native pango-native cairo-native gdk-pixbuf-native"

do_install_append_class-native () {
	create_wrapper ${D}/${bindir}/gtk-update-icon-cache-2.0 \
		GDK_PIXBUF_MODULE_FILE=${STAGING_LIBDIR_NATIVE}/gdk-pixbuf-2.0/${LIBV}/loaders.cache
}

python populate_packages_prepend () {
    gtk_libdir = d.expand('${libdir}/gtk-2.0/${LIBV}')
    immodules_root = os.path.join(gtk_libdir, 'immodules')
    printmodules_root = os.path.join(gtk_libdir, 'printbackends');

    d.setVar('GTKIMMODULES_PACKAGES', ' '.join(do_split_packages(d, immodules_root, '^im-(.*)\.so$', 'gtk-immodule-%s', 'GTK input module for %s')))
    do_split_packages(d, printmodules_root, '^libprintbackend-(.*)\.so$', 'gtk-printbackend-%s', 'GTK printbackend module for %s')

    if (d.getVar('DEBIAN_NAMES', True)):
        d.setVar('PKG_${PN}', '${MLPREFIX}libgtk-2.0')
}
