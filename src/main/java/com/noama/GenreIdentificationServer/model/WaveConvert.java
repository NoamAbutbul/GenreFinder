package com.noama.GenreIdentificationServer.model;

import com.musicg.wave.Wave;
import com.musicg.wave.WaveHeader;
import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

/**
 * MyWave. מחלקת עזר להמרת קבצים
 * By noamabutbul | 07/02/2023 13:53
 */
public abstract class WaveConvert
{
    /**
     * פעולה הממירה את הביטים של הקובץ לשמונה
     * @param filename נתיב הקובץ
     * @return הקובץ המומר
     */
    public static Wave createAndConvertBitsPerSampleTo8(String filename)
    {
        File inputFile = new File(filename);
        final int BITS_PER_SAMPLE_8 = 8;

        int bitsPerSample = BITS_PER_SAMPLE_8; // if we want change num of bits

        ByteArrayOutputStream baos = new ByteArrayOutputStream();

        try ( BufferedInputStream stream = new BufferedInputStream(new FileInputStream(inputFile)))
        {
            int audioData;
            while ((audioData = stream.read()) != -1)
            {
                int audioSample;
                if (bitsPerSample == BITS_PER_SAMPLE_8)
                {
                    audioSample = audioData & 0xff;
                } else
                {
                    audioSample = audioData & 0xffff;
                }
                baos.write(audioSample);
            }
        } catch (IOException e)
        {
            e.printStackTrace();
        }

        byte[] data = baos.toByteArray();
        WaveHeader waveHeader = new WaveHeader();
        waveHeader.setBitsPerSample(bitsPerSample);
        waveHeader.setSampleRate(8000);
        waveHeader.setChannels(1);

        Wave wave = new Wave(waveHeader, data);

        return wave;
    }

}
