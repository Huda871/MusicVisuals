/*assignmentjava
This assignment  visual art piece. A tree will response to the Mp3 musice.
 if you run it then hit space it will work . you will see tree response to the music.
Author: Huda AL Rubayawi
student number: C19738789
Data: 26-04-2021
*/
package C19738789;

// we import here all what we should impoert e.g pApplet, AudioBuffer, end so on
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
    FFT fft; // fast fourier transform. it is mesuerment methode of audio.
    float theta; 
    float a;

    float[] bands;
    float[] smoothedBands;// i used sthomthbands to make the assigment look smother 

/* methode of claculating frquency bands it take fft algorthm and goup of erray together.
separate the audio in diffrent range
*/
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

   // stup fuction here we put the code for mp3 file to let it the music working
    public void setup()
    {

        colorMode(HSB);   // we use HSB coloe mode
        // creat conaction to minum library
        minim = new Minim(this);
        // calling loadfile on minim object
        ap = minim.loadFile("heroplanet.mp3", width);
        //ai = minim.getLineIn(Minim.MONO, width, 44100, 16); 
        ab = ap.mix;
        // creat new fft abject. you pass in frame size and sample rate
        fft = new FFT(width, 44100);

        bands = new float[(int) log2(width)];
        smoothedBands = new float[bands.length];

        /*starMinim();  

        startListening();*/

    }

    // this code is the key function here how let the work  for example pressing space to let it start and if you pre it again it will paus or stop it.
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
    // here amplitude average calcuation to let the tree code response to music connect them to music 
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

     // draw method to show the drwing for example the code of tree  and all code we want to drawing will be here
    public void draw()
    {
        background(0); // black colour of background 
        stroke(255);  // white border colour

        float halfHeight = height / 2;
        for(int i = 0 ; i < ab.size() ; i ++)
        {
            stroke(map(i, 0, ab.size(), 0, 255), 255, 255); // using maping fuction to calculate 
            line(i, halfHeight - (ab.get(i) * halfHeight), i, halfHeight + (ab.get(i) * halfHeight));
        }

        fft.window(FFT.HAMMING);
        fft.forward(ab);
        //we use map to map i from range zero and pass it 255 second two parameters so can HSB color going on
        int highestBand = 0;
        for(int i = 0 ; i < fft.specSize() ; i ++)  // fft array
        {
            stroke(map(i, 0, fft.specSize(), 0, 255), 255, 255);// specSize for to get the size of the buffer
            line(i, height, i, height - (fft.getBand(i) * halfHeight));// getBand to get the element at the position i
            if (fft.getBand(i) > fft.getBand(highestBand))
            {
                highestBand = i;  // we use this to get the highst element
            }
        }

        float freq = fft.indexToFreq(highestBand); // call index frequency and convert it to frequency 
        textSize(24);  // print out the frequency
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
