package com.example.krasn.agent08.Events;

/**
 * Created by krasn on 10/14/2017.
 */

public class NewFileEvent {
    boolean isFileExist =false;
    public NewFileEvent(boolean isFileExist){
        this.isFileExist = isFileExist;
    }

    public boolean isFileNew() {
        return isFileExist;
    }

    public void setFileExist(boolean fileExist) {
        isFileExist = fileExist;
    }
}
