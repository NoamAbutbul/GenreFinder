import sys
import traceback
import librosa

def process_audio(input_file, output_file):
    try:
        # Read the audio data from the input file
        y, sr = librosa.load(input_file)

        # Perform audio processing and feature extraction
        chroma_stft = librosa.feature.chroma_stft(y=y, sr=sr)
        spectral_centroids = librosa.feature.spectral_centroid(y=y, sr=sr)[0]
        spectral_bandwidth = librosa.feature.spectral_bandwidth(y=y, sr=sr)[0]
        y_harm, y_perc = librosa.effects.hpss(y)
        zero_crossings = librosa.zero_crossings(y=y, pad=False)
        rms = librosa.feature.rms(y=y)
        spectral_rolloff = librosa.feature.spectral_rolloff(y=y, sr=sr)[0]
        tempo, _ = librosa.beat.beat_track(y=y)

        # Generate output data or perform additional processing
        featuresExtraction = [chroma_stft.mean(), chroma_stft.var(), rms.mean(), rms.var(), spectral_centroids.mean(), spectral_centroids.var(), spectral_bandwidth.mean(), spectral_bandwidth.var(),
                     spectral_rolloff.mean(), spectral_rolloff.var(), zero_crossings.mean(), zero_crossings.var(), y_harm.mean(), y_harm.var(), y_perc.mean(), y_perc.var(), tempo]


        # Open the output file and write the output
        with open(output_file, 'w') as f:
            # Write the extracted features to the output file
            for feature in [chroma_stft.mean(), chroma_stft.var(), rms.mean(), rms.var(), spectral_centroids.mean(),
                            spectral_centroids.var(), spectral_bandwidth.mean(), spectral_bandwidth.var(),
                            spectral_rolloff.mean(), spectral_rolloff.var(), zero_crossings.mean(),
                            zero_crossings.var(), y_harm.mean(), y_harm.var(), y_perc.mean(), y_perc.var(),
                            tempo]:
                f.write(str(feature) + '\n')

    except Exception as e:
        # Print the traceback of the exception to standard error
        traceback.print_exc(file=sys.stderr)

if __name__ == '__main__':
    # Get the input and output file paths from command-line arguments
    input_file = sys.argv[1]
    output_file = sys.argv[2]

    # Process the audio and generate output
    process_audio(input_file, output_file)
