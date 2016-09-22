package org.daniel.mp3cover.audimmi;

import net.coobird.thumbnailator.Thumbnails;
import org.jaudiotagger.audio.AudioFile;
import org.jaudiotagger.audio.AudioFileIO;
import org.jaudiotagger.tag.TagField;
import org.jaudiotagger.tag.id3.AbstractID3v2Tag;
import org.jaudiotagger.tag.id3.ID3v23Tag;
import org.jaudiotagger.tag.images.Artwork;
import org.jaudiotagger.tag.images.StandardArtwork;
import org.jaudiotagger.tag.reference.PictureTypes;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.Arrays;
import java.util.Iterator;

public class Converter {

    private static final int SIZE = 480;
    private JTextArea log;

    public Converter(JTextArea log) {
        this.log = log;
    }

    public void convert(File dir) {
        Arrays.stream(dir.listFiles((f) -> f.getName().endsWith(".mp3"))).forEach(this::process);
    }

    private void process(File file) {
        try {
            log.append("processing: " + file.getAbsolutePath() + "\n");
            AudioFile audio = AudioFileIO.read(file);
            AbstractID3v2Tag tag = (AbstractID3v2Tag) audio.getTag();
            if (tag == null) {
                log.append("no tag found, skip " + file.getAbsolutePath() + "\n");
                return;
            }
            Artwork artwork = tag.getFirstArtwork();
            if (artwork == null) {
                log.append("no artwork found, skip " + file.getAbsolutePath() + "\n");
                return;
            }
            BufferedImage image = ImageIO.read(new ByteArrayInputStream(artwork.getBinaryData()));
            ByteArrayOutputStream buf = new ByteArrayOutputStream();
            Thumbnails.of(image).size(SIZE, SIZE).outputFormat("jpeg").toOutputStream(buf);
            log.append("image size: " + (buf.size() / 1024) + "KB\n");
            Artwork cover = new StandardArtwork();
            cover.setBinaryData(buf.toByteArray());
            cover.setMimeType("image/jpeg");
            cover.setPictureType(PictureTypes.DEFAULT_ID);
            cover.setWidth(SIZE);
            cover.setHeight(SIZE);
            cover.setDescription("");

            ID3v23Tag newTag = new ID3v23Tag();
            for (Iterator<TagField> iter = tag.getFields(); iter.hasNext();) {
                TagField field = iter.next();
                newTag.setField(field);
            }
            newTag.deleteArtworkField();
            newTag.addField(cover);
            audio.setTag(newTag);
            AudioFileIO.write(audio);
            log.append("processed. \n\n");
        } catch (Exception e) {
            log.append("Cannot process " + file.getAbsolutePath() + " due to " + e.getMessage() + "\n\n");
        }
    }

}
