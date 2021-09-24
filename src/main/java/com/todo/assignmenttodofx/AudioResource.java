package com.todo.assignmenttodofx;

import javafx.scene.media.AudioClip;

public class AudioResource extends Resource {

    private final AudioClip clip;

    public AudioResource(Resource resource) {
        super(resource.getResourceName(), resource.getUrl());
        clip = new AudioClip(resource.getUrl().toExternalForm());
    }

    public AudioClip getClip() {
        return clip;
    }
}
