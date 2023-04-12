package ch.giuntini.stadtlandbutz_host.net;

import ch.giuntini.stadtlandbutz_package.Package;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectStreamClass;

public class PackageObjectInputStream extends ObjectInputStream {

    public PackageObjectInputStream(InputStream in) throws IOException {
        super(in);
    }

    @Override
    protected ObjectStreamClass readClassDescriptor() throws IOException, ClassNotFoundException {
        ObjectStreamClass desc = super.readClassDescriptor();
        if (desc.getName().equals("ch.giuntini.stadtlandbutz_package.Package")) {
            return ObjectStreamClass.lookup(Package.class);
        }
        return desc;
    }
}
