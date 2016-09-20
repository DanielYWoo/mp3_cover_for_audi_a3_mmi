package org.daniel.mp3cover.audimmi;

import org.jaudiotagger.audio.AudioFile;
import org.jaudiotagger.audio.AudioFileIO;
import org.jaudiotagger.tag.id3.AbstractID3v2Tag;
import org.jaudiotagger.tag.images.Artwork;

import java.io.File;
import java.util.Arrays;

// for debug only
public class Analyzer {

    public static void main(String[] args) throws Exception {
        if (args.length == 0) {
            System.out.println("Usage: java org.daniel.mp3cover.audimmi.Converter <dir to convert>");
            System.exit(1);
        }
        Arrays.stream(new File(args[0]).listFiles((f) -> f.getName().endsWith(".mp3"))).
                forEach(Analyzer::process);
        System.out.println("done!");
    }

    private static void process(File file) {
        try {
            AudioFile audio = AudioFileIO.read(file);
            AbstractID3v2Tag tag = (AbstractID3v2Tag) audio.getTag();
            if (tag == null) {
                System.out.println("no tag found, skip " + file.getAbsolutePath());
                return;
            }
            Artwork artwork = tag.getFirstArtwork();
            if (artwork == null) {
                System.out.println("no artwork found, skip " + file.getAbsolutePath());
                return;
            }
            System.out.println("mime type: " + artwork.getMimeType());
            System.out.println("pic type: " + artwork.getPictureType());
            System.out.println("description: " + artwork.getDescription());
            System.out.println("width: " + artwork.getWidth());
            System.out.println("height: " + artwork.getHeight());
            System.out.println("url: " + artwork.getImageUrl());

            System.out.println("processed: " + file.getAbsolutePath());
        } catch (Exception e) {
            System.out.println("Cannot process " + file.getAbsolutePath() + " due to " + e.getMessage());
        }
    }

}
