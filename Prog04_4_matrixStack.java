package jogl_shader_course;

import graphicslib3D.*;
import graphicslib3D.GLSLUtils.*;
import graphicslib3D.shape.Sphere;

import java.nio.*;
import javax.swing.*;

import java.awt.event.KeyEvent;
import java.io.*;

import static com.jogamp.opengl.GL.GL_ARRAY_BUFFER;
import static com.jogamp.opengl.GL.GL_CCW;
import static com.jogamp.opengl.GL.GL_CULL_FACE;
import static com.jogamp.opengl.GL.GL_FLOAT;
import static com.jogamp.opengl.GL.GL_LEQUAL;
import static com.jogamp.opengl.GL.GL_LINES;
import static com.jogamp.opengl.GL.GL_STATIC_DRAW;
import static com.jogamp.opengl.GL.GL_TEXTURE0;
import static com.jogamp.opengl.GL.GL_TEXTURE_2D;
import static com.jogamp.opengl.GL.GL_TRIANGLES;
import static com.jogamp.opengl.GL2ES2.GL_FRAGMENT_SHADER;
import static com.jogamp.opengl.GL2ES2.GL_VERTEX_SHADER;
import static com.jogamp.opengl.GL4.*;
import com.jogamp.opengl.*;
import com.jogamp.opengl.awt.GLCanvas;
import com.jogamp.opengl.util.*;
import com.jogamp.opengl.util.texture.Texture;
import com.jogamp.opengl.util.texture.TextureIO;
import com.jogamp.common.nio.Buffers;

import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

public class Prog04_4_matrixStack extends JFrame implements GLEventListener {
	private GLCanvas myCanvas;
	private int rendering_program;
	private int vao[] = new int[1];
	private int vbo[] = new int[8];
	private int vis = 1;
	private int vis2 = 1;
	private float cameraX, cameraY, cameraZ;
	private float rotX, rotY, rotZ;
	private float sphLocX, sphLocY, sphLocZ;
	private float LocX, LocY, LocZ;

	private GLSLUtils util = new GLSLUtils();

	// earth
	private int earthTexture;
	private Texture joglEarthTexture;

	// mars
	private int marsTexture;
	private Texture joglMarsTexture;

	// earth's moon
	private int moonEarthTexture;
	private Texture joglmoonEarthTexture;

	// mars's moon
	private int moonMarsTexture;
	private Texture joglMoonMarsTexture;

	// sun
	private int sunTexture;
	private Texture joglSunTexture;

	// red
	private int redTexture;
	private Texture joglRedTexture;

	// green
	private int greenTexture;
	private Texture joglGreenTexture;

	// blue
	private int blueTexture;
	private Texture joglBlueTexture;

	// prism
	private int prismTexture;
	private Texture joglPrismTexture;

	private Sphere mySphere = new Sphere(24);

	private MatrixStack mvStack = new MatrixStack(20);

	public Prog04_4_matrixStack() {
		setTitle("Chapter4 - program4");
		setSize(1250, 1000);
		// Making sure we get a GL4 context for the canvas
		GLProfile profile = GLProfile.get(GLProfile.GL4);
		GLCapabilities capabilities = new GLCapabilities(profile);
		myCanvas = new GLCanvas(capabilities);
		// end GL4 context
		myCanvas.addGLEventListener(this);
		getContentPane().add(myCanvas);
		this.setVisible(true);
		FPSAnimator animator = new FPSAnimator(myCanvas, 50);
		animator.start();

	}

	private void drawPlanet(GL4 gl, int mv_loc, int numVerts, int planet) {
		gl.glUniformMatrix4fv(mv_loc, 1, false, mvStack.peek().getFloatValues(), 0); // Draw rotated sun
		gl.glBindBuffer(GL_ARRAY_BUFFER, vbo[0]);
		gl.glVertexAttribPointer(0, 3, GL_FLOAT, false, 0, 0);
		gl.glEnableVertexAttribArray(0);
		gl.glBindBuffer(GL_ARRAY_BUFFER, vbo[1]);
		gl.glVertexAttribPointer(1, 2, GL_FLOAT, false, 0, 0);
		gl.glEnableVertexAttribArray(1);
		gl.glActiveTexture(GL_TEXTURE0);
		switch (planet) {
		case 1:
			planet = 1;
			gl.glBindTexture(GL_TEXTURE_2D, sunTexture);
			break;
		case 2:
			planet = 2;
			gl.glBindTexture(GL_TEXTURE_2D, earthTexture);
			break;
		case 3:
			planet = 3;
			gl.glBindTexture(GL_TEXTURE_2D, moonEarthTexture);
			break;
		case 4:
			planet = 4;
			gl.glBindTexture(GL_TEXTURE_2D, marsTexture);
			break;
		case 5:
			planet = 5;
			gl.glBindTexture(GL_TEXTURE_2D, moonMarsTexture);
			break;
		default:
			planet = 6;
			gl.glBindTexture(GL_TEXTURE_2D, sunTexture);
			break;
		}
		gl.glEnable(GL_CULL_FACE);
		gl.glFrontFace(GL_CCW);
		gl.glEnable(GL_DEPTH_TEST);
		gl.glDrawArrays(GL_TRIANGLES, 0, numVerts);
	}

	private void drawX(GL4 gl, int mv_loc) {
		gl.glUniformMatrix4fv(mv_loc, 1, false, mvStack.peek().getFloatValues(), 0); // Draw rotated sun

		gl.glBindBuffer(GL_ARRAY_BUFFER, vbo[3]);
		gl.glVertexAttribPointer(0, 3, GL_FLOAT, false, 0, 0);
		gl.glEnableVertexAttribArray(0);

		gl.glActiveTexture(GL_TEXTURE0);
		gl.glBindTexture(GL_TEXTURE_2D, redTexture);

		gl.glEnable(GL_CULL_FACE);
		gl.glFrontFace(GL_CCW);
		gl.glEnable(GL_DEPTH_TEST);
		gl.glDrawArrays(GL_TRIANGLES, 0, 36);
	}

	private void drawY(GL4 gl, int mv_loc) {
		gl.glUniformMatrix4fv(mv_loc, 1, false, mvStack.peek().getFloatValues(), 0); // Draw rotated sun

		gl.glBindBuffer(GL_ARRAY_BUFFER, vbo[4]);
		gl.glVertexAttribPointer(0, 3, GL_FLOAT, false, 0, 0);
		gl.glEnableVertexAttribArray(0);

		gl.glActiveTexture(GL_TEXTURE0);
		gl.glBindTexture(GL_TEXTURE_2D, greenTexture);

		gl.glEnable(GL_CULL_FACE);
		gl.glFrontFace(GL_CCW);
		gl.glEnable(GL_DEPTH_TEST);
		gl.glDrawArrays(GL_TRIANGLES, 0, 36);
	}

	private void drawZ(GL4 gl, int mv_loc) {
		gl.glUniformMatrix4fv(mv_loc, 1, false, mvStack.peek().getFloatValues(), 0); // Draw rotated sun

		gl.glBindBuffer(GL_ARRAY_BUFFER, vbo[5]);
		gl.glVertexAttribPointer(0, 3, GL_FLOAT, false, 0, 0);
		gl.glEnableVertexAttribArray(0);

		gl.glActiveTexture(GL_TEXTURE0);
		gl.glBindTexture(GL_TEXTURE_2D, blueTexture);

		gl.glEnable(GL_CULL_FACE);
		gl.glFrontFace(GL_CCW);
		gl.glEnable(GL_DEPTH_TEST);
		gl.glDrawArrays(GL_TRIANGLES, 0, 36);
	}

	private void drawP(GL4 gl, int mv_loc) {
		gl.glUniformMatrix4fv(mv_loc, 1, false, mvStack.peek().getFloatValues(), 0); // Draw rotated sun

		gl.glBindBuffer(GL_ARRAY_BUFFER, vbo[6]);
		gl.glVertexAttribPointer(0, 3, GL_FLOAT, false, 0, 0);
		gl.glEnableVertexAttribArray(0);

		gl.glBindBuffer(GL_ARRAY_BUFFER, vbo[7]);
		gl.glVertexAttribPointer(1, 2, GL_FLOAT, false, 0, 0);
		gl.glEnableVertexAttribArray(1);

		gl.glActiveTexture(GL_TEXTURE0);
		gl.glBindTexture(GL_TEXTURE_2D, prismTexture);

		gl.glEnable(GL_CULL_FACE);
		gl.glFrontFace(GL_CCW);
		gl.glEnable(GL_DEPTH_TEST);
		gl.glDrawArrays(GL_TRIANGLES, 0, 72);
	}

	public void display(GLAutoDrawable drawable) {
		BufferedReader in = new BufferedReader(new InputStreamReader(System.in));

		GL4 gl = (GL4) GLContext.getCurrentGL();

		gl.glClear(GL_DEPTH_BUFFER_BIT);
		float bkg[] = { 0.0f, 0.0f, 0.0f, 1.0f };
		FloatBuffer bkgBuffer = Buffers.newDirectFloatBuffer(bkg);
		gl.glClearBufferfv(GL_COLOR, 0, bkgBuffer);
		gl.glClear(GL_DEPTH_BUFFER_BIT);
		gl.glUseProgram(rendering_program);

		int mv_loc = gl.glGetUniformLocation(rendering_program, "mv_matrix");
		int proj_loc = gl.glGetUniformLocation(rendering_program, "proj_matrix");
		int numVerts = mySphere.getIndices().length;

		float aspect = (float) myCanvas.getWidth() / (float) myCanvas.getHeight();
		Matrix3D pMat = perspective(60.0f, aspect, 0.1f, 1000.0f);

		// push view matrix onto the stack
		mvStack.pushMatrix(); // Save global reference system		
		
		addKeyListener(new KeyAdapter() {
			@Override
			public void keyPressed(KeyEvent e) {
				int keyCode = e.getKeyCode();
				if (keyCode == 81) {// q, move up
					cameraY = cameraY + 0.0005f;
				}
				if (keyCode == 65) {// a, strafe left
					cameraX = cameraX - 0.0005f;
				}
				if (keyCode == 69) {// e, move down
					cameraY = cameraY - 0.0005f;
				}
				if (keyCode == 68) {// d, strafe right
					cameraX = cameraX + 0.0005f;
				}
				if (keyCode == 87) {// w, move forward
					cameraZ = cameraZ - 0.0005f;
				}
				if (keyCode == 83) {// s move backward
					cameraZ = cameraZ + 0.0005f;
				}
				if (keyCode == 39) {// --> pan right
					rotY = rotY + 0.1f;
					mvStack.rotate(0.005, 0.0, 0.1, 0.0); 
				}
				if (keyCode == 37) {// <-- pan left
					rotY = rotY - 0.1f;
					mvStack.rotate(0.005, 0.0, -0.1, 0.0); 
				}
				if (keyCode == 38) {// |^ pitch up
					rotX = rotX - 0.1f;
					mvStack.rotate(0.005, -0.1, 00.0, 0.0); 
				}
				if (keyCode == 40) {// |v pitch down
					rotX = rotX + 0.1f;
					mvStack.rotate(0.005, 0.1, 0.0, 0.0); 
				}
				if (keyCode == 32) {
					if (vis > 0) {
						vis = -1;
					} else {
						vis = 1;
					}
				}
			}
		});
		
		mvStack.translate(-cameraX, -cameraY, -cameraZ);
		
		double amt = (double) (System.currentTimeMillis()) / 1000.0;

		gl.glUniformMatrix4fv(proj_loc, 1, false, pMat.getFloatValues(), 0);
		mvStack.pushMatrix(); // Save camera's reference system
		// mvStack.rotate((System.currentTimeMillis()) / 100.0, 1.0, 1.0, 1.0); //
		// Rotate camera

		// ----------------------XYZ cords
		mvStack.translate(LocX, LocY, LocZ); // Translate to origin
		mvStack.pushMatrix(); // Save sun's position
		if (vis > 0) {
			drawX(gl, mv_loc);
			drawY(gl, mv_loc);
			drawZ(gl, mv_loc);
		}
		mvStack.popMatrix(); // Go back to sun's position (no rotation)

		// ----------------------sun
		mvStack.translate(sphLocX, sphLocY, sphLocZ); // Translate sun to position
		mvStack.pushMatrix(); // Save sun's position
		mvStack.rotate((System.currentTimeMillis()) / 10.0, 0.0, 1.0, 0.0); // Rotate sun
		mvStack.scale(1.5, 1.5, 1.5); // Scale object
		drawPlanet(gl, mv_loc, numVerts, 1);
		mvStack.popMatrix(); // Go back to sun's position (no rotation)

		// -----------------------earth
		mvStack.pushMatrix(); // Save sun's position (no rotation)
		mvStack.translate(Math.sin(amt) * 3.0f, 0.0f, Math.cos(amt) * 3.0f); // Move to earth's position
		mvStack.pushMatrix(); // Save earth's position - translated
		mvStack.rotate((System.currentTimeMillis()/2) / 10.0, 0.5, 1.0, 0.0); // Rotate the earth
		mvStack.scale(0.75, 0.75, 0.75); // Scale object
		drawPlanet(gl, mv_loc, numVerts, 2);
		mvStack.popMatrix(); // Go back to earth's position - translated

		// -----------------------earth's moon
		mvStack.pushMatrix(); // Save planet's position - translated (no need to save if we only have one
								// moon)
		mvStack.translate(0.0f, -Math.sin(amt) * 1.33f, Math.cos(amt) * 1.33f); // Translate WRT planet
		mvStack.rotate((System.currentTimeMillis()/5) / 10.0, 0.0, 1.0, 0.0); // Rotate WRT planet x-axis
		mvStack.scale(0.25, 0.25, 0.25); // Scale object
		drawPlanet(gl, mv_loc, numVerts, 3);
		mvStack.popMatrix();// Go back to planet's position - translated
		mvStack.popMatrix();// Go back sun's position

		// -----------------------mars
		mvStack.pushMatrix(); // Save sun's position (no rotation)
		mvStack.translate(Math.sin(amt) * 5.0f, Math.sin(amt) * 1.0f, Math.cos(amt) * 5.0f); // Move to mars's position
		mvStack.pushMatrix(); // Save mars's position - translated
		mvStack.rotate((System.currentTimeMillis()/7) / 10.0, 0.0, -1.0, 0.0); // Rotate mars
		mvStack.scale(0.75, 0.75, 0.75); // Scale object
		drawPlanet(gl, mv_loc, numVerts, 4);
		mvStack.popMatrix(); // Go back to mars's position - translated

		// -----------------------mars's moon
		mvStack.pushMatrix(); // Save planet's position - translated
		mvStack.translate(Math.sin(amt) * 1.33f, Math.sin(amt) * 1.33f, Math.cos(amt) * 1.33f); // Translate WRT planet
		mvStack.rotate((System.currentTimeMillis()/10) / 10.0, 0.0, 2.0, 5.0); // Rotate WRT planet x-axis
		mvStack.scale(0.25, 0.25, 0.25); // Scale object
		drawPlanet(gl, mv_loc, numVerts, 5);
		mvStack.popMatrix();// Go back to planet's position - translated
		mvStack.popMatrix();// Go back sun's position

		// -----------------------neptune
		mvStack.pushMatrix(); // Save sun's position (no rotation)
		mvStack.translate(Math.sin(amt) * 7.0f, -Math.sin(amt) * 2.0f, Math.cos(amt) * 7.0f); // Move to neptunes's
																								// position
		mvStack.pushMatrix(); // Save neptunes's position - translated
		mvStack.rotate((System.currentTimeMillis()/3) / 10.0, -2.0, 1.0, -6.0); // Rotate neptune
		mvStack.scale(1.75, 1.75, 1.75); // Scale object
		drawP(gl, mv_loc);

		mvStack.popMatrix(); // Go back to neptune's position - translated
		mvStack.popMatrix();// Go back to sun's position
		mvStack.popMatrix();// Go back to camera's reference
		mvStack.popMatrix();// Go back to global reference

	}

	public void init(GLAutoDrawable drawable) {
		GL4 gl = (GL4) GLContext.getCurrentGL();
		rendering_program = createShaderProgram();
		setupVertices();
		cameraX = 0.0f;
		cameraY = 0.0f;
		cameraZ = 12.0f;
		sphLocX = 0.0f;
		sphLocY = 0.0f;
		sphLocZ = 0.0f;
		rotX = 0.0f;
		rotY = 0.0f;
		rotZ = 0.0f;
		LocX = 0.0f;
		LocY = 0.0f;
		LocZ = 0.0f;

		joglEarthTexture = loadTexture("ModelsTextures/PlanetPixelEmporium/earthmap1k.jpg");
		earthTexture = joglEarthTexture.getTextureObject();

		joglMarsTexture = loadTexture("ModelsTextures/PlanetPixelEmporium/mars.jpg");
		marsTexture = joglMarsTexture.getTextureObject();

		joglmoonEarthTexture = loadTexture("ModelsTextures/PlanetPixelEmporium/moon.jpg");
		moonEarthTexture = joglmoonEarthTexture.getTextureObject();

		joglMoonMarsTexture = loadTexture("ModelsTextures/PlanetPixelEmporium/moon2.png");
		moonMarsTexture = joglMoonMarsTexture.getTextureObject();

		joglSunTexture = loadTexture("ModelsTextures/PlanetPixelEmporium/sunmap.jpg");
		sunTexture = joglSunTexture.getTextureObject();

		joglRedTexture = loadTexture("ModelsTextures/PlanetPixelEmporium/red.png");
		redTexture = joglRedTexture.getTextureObject();

		joglGreenTexture = loadTexture("ModelsTextures/PlanetPixelEmporium/green.png");
		greenTexture = joglGreenTexture.getTextureObject();

		joglBlueTexture = loadTexture("ModelsTextures/PlanetPixelEmporium/blue.jpg");
		blueTexture = joglBlueTexture.getTextureObject();

		joglPrismTexture = loadTexture("ModelsTextures/me.jpg");
		prismTexture = joglPrismTexture.getTextureObject();
	}

	private void setupVertices() {
		GL4 gl = (GL4) GLContext.getCurrentGL();

		Vertex3D[] vertices = mySphere.getVertices();
		int[] indices = mySphere.getIndices();

		float[] pvalues = new float[indices.length * 3];
		float[] tvalues = new float[indices.length * 2];
		float[] nvalues = new float[indices.length * 3];

		for (int i = 0; i < indices.length; i++) {
			pvalues[i * 3] = (float) (vertices[indices[i]]).getX();
			pvalues[i * 3 + 1] = (float) (vertices[indices[i]]).getY();
			pvalues[i * 3 + 2] = (float) (vertices[indices[i]]).getZ();
			tvalues[i * 2] = (float) (vertices[indices[i]]).getS();
			tvalues[i * 2 + 1] = (float) (vertices[indices[i]]).getT();
			nvalues[i * 3] = (float) (vertices[indices[i]]).getNormalX();
			nvalues[i * 3 + 1] = (float) (vertices[indices[i]]).getNormalY();
			nvalues[i * 3 + 2] = (float) (vertices[indices[i]]).getNormalZ();
		}

		float p = 0.01f;
		float n = -0.01f;
		float pp = 1000.00f;
		float nn = -1000.00f;

		float[] xAxis =
				// back square
				{ nn, p, n, nn, n, n, pp, n, n, // BBT
						pp, n, n, pp, p, n, nn, p, n, // BTT
						// right square
						pp, n, n, pp, n, p, pp, p, n, // RBT
						pp, n, p, pp, p, p, pp, p, n, // RTT
						// front square
						pp, n, p, nn, n, p, pp, p, p, // BT
						nn, n, p, nn, p, p, pp, p, p, // TT
						// let square
						nn, n, p, nn, n, n, nn, p, p, // LBT
						nn, n, n, nn, p, n, nn, p, p, // LTT
						// bottom square
						nn, n, p, pp, n, p, pp, n, n, // BoT
						pp, n, n, nn, n, n, nn, n, p, // BoBT
						// top square
						nn, p, n, pp, p, n, pp, p, p, // ToBT
						pp, p, p, nn, p, p, nn, p, n // Tot
				};

		float[] yAxis =
				// back square
				{ n, pp, n, n, nn, n, p, nn, n, // BBT
						p, nn, n, p, pp, n, n, pp, n, // BTT
						// right square
						p, nn, n, p, nn, p, p, pp, n, // RBT
						p, nn, p, p, pp, p, p, pp, n, // RTT
						// front square
						p, nn, p, n, nn, p, p, pp, p, // BT
						n, nn, p, n, pp, p, p, pp, p, // TT
						// let square
						n, nn, p, n, nn, n, n, pp, p, // LBT
						n, nn, n, n, pp, n, n, pp, p, // LTT
						// bottom square
						n, nn, p, p, nn, p, p, nn, n, // BoT
						p, nn, n, n, nn, n, n, nn, p, // BoBT
						// top square
						n, pp, n, p, pp, n, p, pp, p, // ToBT
						p, pp, p, n, pp, p, n, pp, n // Tot
				};

		float[] zAxis = { n, p, nn, n, n, nn, p, n, nn, // BBT
				p, n, nn, p, p, nn, n, p, nn, // BTT
				// right square
				p, n, nn, p, n, pp, p, p, nn, // RBT
				p, n, pp, p, p, pp, p, p, nn, // RTT
				// front square
				p, n, pp, n, n, pp, p, p, pp, // BT
				n, n, pp, n, p, pp, p, p, pp, // TT
				// let square
				n, n, pp, n, n, nn, n, p, pp, // LBT
				n, n, nn, n, p, nn, n, p, pp, // LTT
				// bottom square
				n, n, pp, p, n, pp, p, n, nn, // BoT
				p, n, nn, n, n, nn, n, n, pp, // BoBT
				// top square
				n, p, nn, p, p, nn, p, p, pp, // ToBT
				p, p, pp, n, p, pp, n, p, nn // Tot
		};

		float[] pentagonalPrism = {
				 0.500f,  0.500f, -0.000f,//A
				 0.500f, -0.500f, -0.000f,//B 
				 0.250f, -0.500f, -0.433f,//C
				 
				 0.250f, -0.500f, -0.433f,//C
				 0.250f,  0.500f, -0.433f,//D
				 0.500f,  0.500f, -0.000f,//A
				 
				 0.250f,  0.500f, -0.433f,//E
				 0.250f, -0.500f, -0.433f,//F
				-0.250f, -0.500f, -0.433f,//G
				
				-0.250f, -0.500f, -0.433f,//G
				-0.250f,  0.500f, -0.433f,//H
				 0.250f,  0.500f, -0.433f,//E
				
				-0.250f,  0.500f, -0.433f,//I
				-0.250f, -0.500f, -0.433f,//J
				-0.500f, -0.500f,  0.000f,//K
				
				-0.500f, -0.500f,  0.000f,//K
				-0.500f,  0.500f,  0.000f,//L
				-0.250f,  0.500f, -0.433f,//I
				
				-0.500f,  0.500f,  0.000f,//M
				-0.500f, -0.500f,  0.000f,//N
				-0.250f, -0.500f,  0.433f,//O
				
				-0.250f, -0.500f,  0.433f,//O
				-0.250f,  0.500f,  0.433f,//P
				-0.500f,  0.500f,  0.000f,//M
				
				-0.250f,  0.500f,  0.433f,//Q
				-0.250f, -0.500f,  0.433f,//R
				 0.250f, -0.500f,  0.433f,//S
				 
				 0.250f, -0.500f,  0.433f,//S
				 0.250f,  0.500f,  0.433f,//T
				-0.250f,  0.500f,  0.433f,//Q
				 
				 0.250f,  0.500f,  0.433f,//U
				 0.250f, -0.500f,  0.433f,//V
				 0.500f, -0.500f, -0.000f,//W
				 
				 0.500f, -0.500f, -0.000f,//W
				 0.500f,  0.500f, -0.000f,//Z
				 0.250f,  0.500f,  0.433f,//U
				
				 0.500f,  0.500f, -0.000f,//A
				 0.250f,  0.500f, -0.433f,//B
				 0.000f,  0.500f,  0.000f,//C
				 
				 0.250f,  0.500f, -0.433f,//D
				-0.250f,  0.500f, -0.433f,//E
				 0.000f,  0.500f,  0.000f,//F
				 
				-0.250f,  0.500f, -0.433f,//G
				-0.500f,  0.500f,  0.000f,//H
				 0.000f,  0.500f,  0.000f,//I
				 
				-0.500f,  0.500f,  0.000f,//J
				-0.250f,  0.500f,  0.433f,//K
				 0.000f,  0.500f,  0.000f,//L
				 
				-0.250f,  0.500f,  0.433f,//M
				 0.250f,  0.500f,  0.433f,//N
				 0.000f,  0.500f,  0.000f,//O
				 
				 0.250f,  0.500f,  0.433f,//P
				 0.500f,  0.500f, -0.000f,//Q
				 0.000f,  0.500f,  0.000f,//R
				 
				 0.250f, -0.500f, -0.433f,//A
				 0.500f, -0.500f, -0.000f,//B
				 0.000f, -0.500f,  0.000f,//C
				 
				-0.250f, -0.500f, -0.433f,//D
				 0.250f, -0.500f, -0.433f,//E
				 0.000f, -0.500f,  0.000f,//F
				 
				-0.500f, -0.500f,  0.000f,//G
				-0.250f, -0.500f, -0.433f,//H
				 0.000f, -0.500f,  0.000f,//I
				 
				-0.250f, -0.500f,  0.433f,//J
				-0.500f, -0.500f,  0.000f,//K
				 0.000f, -0.500f,  0.000f,//L
				 
				 0.250f, -0.500f,  0.433f,//M
				-0.250f, -0.500f,  0.433f,//N
				 0.000f, -0.500f,  0.000f,//O
				 
				 0.500f, -0.500f, -0.000f,//P
				 0.250f, -0.500f,  0.433f,//Q
				 0.000f, -0.500f,  0.000f,//R
		};
		
		float[] texture_coordinates =
		{	
				0.0f, 0.0f, 0.0f, 1.0f, 1.0f, 1.0f,
				1.0f, 1.0f, 1.0f, 0.0f, 0.0f, 0.0f,
				0.0f, 0.0f, 0.0f, 1.0f, 1.0f, 1.0f,
				1.0f, 1.0f, 1.0f, 0.0f, 0.0f, 0.0f,
				0.0f, 0.0f, 0.0f, 1.0f, 1.0f, 1.0f,
				1.0f, 1.0f, 1.0f, 0.0f, 0.0f, 0.0f,
				0.0f, 0.0f, 0.0f, 1.0f, 1.0f, 1.0f,
				1.0f, 1.0f, 1.0f, 0.0f, 0.0f, 0.0f,
				0.0f, 0.0f, 0.0f, 1.0f, 1.0f, 1.0f,
				1.0f, 1.0f, 1.0f, 0.0f, 0.0f, 0.0f,
				0.0f, 0.0f, 0.0f, 1.0f, 1.0f, 1.0f,
				1.0f, 1.0f, 1.0f, 0.0f, 0.0f, 0.0f,
					
		};
		
		gl.glGenVertexArrays(vao.length, vao, 0);
		gl.glBindVertexArray(vao[0]);
		gl.glGenBuffers(8, vbo, 0);

		gl.glBindBuffer(GL_ARRAY_BUFFER, vbo[0]);
		FloatBuffer vertBuf = Buffers.newDirectFloatBuffer(pvalues);
		gl.glBufferData(GL_ARRAY_BUFFER, vertBuf.limit() * 4, vertBuf, GL_STATIC_DRAW);

		gl.glBindBuffer(GL_ARRAY_BUFFER, vbo[1]);
		FloatBuffer texBuf = Buffers.newDirectFloatBuffer(tvalues);
		gl.glBufferData(GL_ARRAY_BUFFER, texBuf.limit() * 4, texBuf, GL_STATIC_DRAW);

		gl.glBindBuffer(GL_ARRAY_BUFFER, vbo[2]);
		FloatBuffer norBuf = Buffers.newDirectFloatBuffer(nvalues);
		gl.glBufferData(GL_ARRAY_BUFFER, norBuf.limit() * 4, norBuf, GL_STATIC_DRAW);

		gl.glBindBuffer(GL_ARRAY_BUFFER, vbo[3]);
		FloatBuffer xBuf = Buffers.newDirectFloatBuffer(xAxis);
		gl.glBufferData(GL_ARRAY_BUFFER, xBuf.limit() * 4, xBuf, GL_STATIC_DRAW);

		gl.glBindBuffer(GL_ARRAY_BUFFER, vbo[4]);
		FloatBuffer yBuf = Buffers.newDirectFloatBuffer(yAxis);
		gl.glBufferData(GL_ARRAY_BUFFER, yBuf.limit() * 4, yBuf, GL_STATIC_DRAW);

		gl.glBindBuffer(GL_ARRAY_BUFFER, vbo[5]);
		FloatBuffer zBuf = Buffers.newDirectFloatBuffer(zAxis);
		gl.glBufferData(GL_ARRAY_BUFFER, zBuf.limit() * 4, zBuf, GL_STATIC_DRAW);

		gl.glBindBuffer(GL_ARRAY_BUFFER, vbo[6]);
		FloatBuffer vertBuf2 = Buffers.newDirectFloatBuffer(pentagonalPrism);
		gl.glBufferData(GL_ARRAY_BUFFER, vertBuf2.limit()*4, vertBuf2, GL_STATIC_DRAW);
		
		gl.glBindBuffer(GL_ARRAY_BUFFER, vbo[7]);
		FloatBuffer texBuf2 = Buffers.newDirectFloatBuffer(texture_coordinates);
		gl.glBufferData(GL_ARRAY_BUFFER, texBuf2.limit() * 4, texBuf2, GL_STATIC_DRAW);

	}

	private Matrix3D perspective(float fovy, float aspect, float n, float f) {
		float q = 1.0f / ((float) Math.tan(Math.toRadians(0.5f * fovy)));
		float A = q / aspect;
		float B = (n + f) / (n - f);
		float C = (2.0f * n * f) / (n - f);
		Matrix3D r = new Matrix3D();
		r.setElementAt(0, 0, A);
		r.setElementAt(1, 1, q);
		r.setElementAt(2, 2, B);
		r.setElementAt(3, 2, -1.0f);
		r.setElementAt(2, 3, C);
		return r;
	}

	public static void main(String[] args) {
		SwingUtilities.invokeLater(() -> {
			Prog04_4_matrixStack f = new Prog04_4_matrixStack();
			f.setFocusable(true);
			f.setVisible(true);
		});
	}

	public void reshape(GLAutoDrawable drawable, int x, int y, int width, int height) {
	}

	public void dispose(GLAutoDrawable drawable) {
	}

	private int createShaderProgram() {
		GL4 gl = (GL4) GLContext.getCurrentGL();

		String vshaderSource[] = util.readShaderSource("ProgData/Prog6_1b_data/vert.shader");
		String fshaderSource[] = util.readShaderSource("ProgData/Prog6_1b_data/frag.shader");

		int vShader = gl.glCreateShader(GL_VERTEX_SHADER);
		int fShader = gl.glCreateShader(GL_FRAGMENT_SHADER);

		gl.glShaderSource(vShader, vshaderSource.length, vshaderSource, null, 0);
		gl.glShaderSource(fShader, fshaderSource.length, fshaderSource, null, 0);
		gl.glCompileShader(vShader);
		gl.glCompileShader(fShader);

		int vfprogram = gl.glCreateProgram();
		gl.glAttachShader(vfprogram, vShader);
		gl.glAttachShader(vfprogram, fShader);
		gl.glLinkProgram(vfprogram);
		return vfprogram;
	}

	public Texture loadTexture(String textureFileName) {
		Texture tex = null;
		try {
			tex = TextureIO.newTexture(new File(textureFileName), false);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return tex;
	}

	public void keyPressed(KeyEvent event) {
	}

}