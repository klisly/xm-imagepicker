package cn.iterlog.xmimagepicker.model;

import java.net.URI;

public class Media {
    private URI path;
    private int type;

    public Media(URI path, int type) {
        this.path = path;
        this.type = type;
    }

    public URI getPath() {
        return path;
    }

    public void setPath(URI path) {
        this.path = path;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }
}
