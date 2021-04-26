/*assignmentjava
This assignment  visual art piece. A tree will response to the Mp3 musice 
Author: Huda AL Rubayawi
student number: C19738789
Data: 26-04-2021
*/
package C19738789;

import ddf.minim.AudioBuffer;
import ddf.minim.AudioInput;
import ddf.minim.AudioPlayer;
import ddf.minim.Minim;
import ddf.minim.analysis.FFT;
import ie.tudublin.Visual;
import ie.tudublin.VisualException;
import processing.core.PApplet;

public class Assignment extends PApplet{

    Minim minim;// Connect to minim
    AudioPlayer ap;// to play mp3 music
    AudioBuffer ab;// Samples
    //AudioInput ai;// How to connect to mic
    FFT fft;
    float theta; 
    float a;

    float[] bands;
    float[] smoothedBands;

    void calculateFrequencyBands() {
        for (int i = 0; i < bands.length; i++) {
          int start = (int) pow(2, i) - 1;
          int w = (int) pow(2, i);
          int end = start + w;
          float average = 0;
          for (int j = start; j < end; j++) {
            average += fft.getBand(j) * (j + 1);
          }
          average /= (float) w;
          bands[i] = average * 5.0f;
          smoothedBands[i] = lerp(smoothedBands[i], bands[i], 0.05f);
        }
      }


    public void settings()
    {
        size(1024, 500);
        //fullScreen(P3D, SPAN); // Try this for full screen multiple monitor support :-) Be careful of exceptions!

    }
    float log2(float f) {
        return log(f) / log(2.0f);
    }
    int which = 0;


    public void setup()
    {

        colorMode(HSB);

        minim = new Minim(this);
        ap = minim.loadFile("heroplanet.mp3", width);
        //ai = minim.getLineIn(Minim.MONO, width, 44100, 16); 
        ab = ap.mix;

        fft = new FFT(width, 44100);

        bands = new float[(int) log2(width)];
        smoothedBands = new float[bands.length];

        /*starMinim();  

        startListening();*/

    }


    public void keyPressed()
    {
        if (keyCode >= '0' && keyCode <= '5') {
            which = keyCode - '0';
        }
        if (keyCode == ' ')
        {
            if (ap.isPlaying())
            {
                ap.pause();
            }
            else
            {
                ap.rewind();
                ap.play();
            }
        }
    }

    private float amplitude  = 0;
	private float smothedAmplitude = 0;

    public void calculateAverageAmplitude()
	{
		float total = 0;
		for(int i = 0 ; i < ab.size() ; i ++)
        {
			total += abs(ab.get(i));
		}
		amplitude = total / ab.size();
		smothedAmplitude = PApplet.lerp(smothedAmplitude, amplitude, 0.1f);
	}
        /*{
            getAudioPlayer()cue(0);  
            getAudioPlayer().player();
        }*/


    public void draw()
    {
        background(0);
        stroke(255);

        float halfHeight = height / 2;
        for(int i = 0 ; i < ab.size() ; i ++)
        {
            stroke(map(i, 0, ab.size(), 0, 255), 255, 255);
            line(i, halfHeight - (ab.get(i) * halfHeight), i, halfHeight + (ab.get(i) * halfHeight));
        }

        fft.window(FFT.HAMMING);
        fft.forward(ab);

        int highestBand = 0;
        for(int i = 0 ; i < fft.specSize() ; i ++)  // fft array
        {
            stroke(map(i, 0, fft.specSize(), 0, 255), 255, 255);// specSize for input
            line(i, height, i, height - (fft.getBand(i) * halfHeight));// getBand for output
            if (fft.getBand(i) > fft.getBand(highestBand))
            {
                highestBand = i;
            }
        }

        float freq = fft.indexToFreq(highestBand);
        textSize(24);
        fill(255);
        text("Frequency: " + freq, 10, 50);
        //text("Note: " + spell(freq), 10, 100);

        calculateFrequencyBands();
        calculateAverageAmplitude();

        frameRate(30);
        stroke(255);
        // Let's pick an angle 0 to 90 degrees based on the mouse position
        //float a = (mouseX / (float) width) * 90f;
        a = smothedAmplitude * 700;
        // Convert it to radians
        theta = radians(a);
        // Start the tree from the bottom of the screen
        translate(width/2,height);
        // Draw a line 120 pixels
        line(0,0,0,-120);
        // Move to the end of that line
        translate(0,-120);
        // Start the recursive branching!
        branch(120);

        }

        void branch(float h) {
        // Each branch will be 2/3rds the size of the previous one
        h *= 0.66;
        
        // All recursive functions must have an exit condition!!!!
        // Here, ours is when the length of the branch is 2 pixels or less
        if (h > 2) {
            pushMatrix();    // Save the current state of transformation (i.e. where are we now)
            rotate(theta);   // Rotate by theta
            line(0, 0, 0, -h);  // Draw the branch
            translate(0, -h); // Move to the end of the branch
            branch(h);       // Ok, now call myself to draw two new branches!!
            popMatrix();     // Whenever we get back here, we "pop" in order to restore the previous matrix state
            
            // Repeat the same thing, only branch off to the "left" this time!
            pushMatrix();
            rotate(-theta);
            line(0, 0, 0, -h);
            translate(0, -h);
            branch(h);
            popMatrix();
        }
    }
    

    


       /* calculateFFt();
    }
    catch(VisualException e)
    {
        e.printstackTrace();

    }*/
 


}
