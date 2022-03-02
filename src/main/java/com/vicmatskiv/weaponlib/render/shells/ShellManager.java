package com.vicmatskiv.weaponlib.render.shells;

import java.util.ArrayList;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.SynchronousQueue;

import com.vicmatskiv.weaponlib.compatibility.CompatibleShellRenderer;
import com.vicmatskiv.weaponlib.render.shells.ShellParticleSimulator.Shell;

/**
 * Since the renderer and the physics are kept in separate classes, this helps
 * manage it.
 * 
 *
 */
public class ShellManager {

	private ShellParticleSimulator shellParticleSimulator = new ShellParticleSimulator();

	private ArrayList<Shell> shells = new ArrayList<Shell>();
	private LinkedBlockingQueue<Shell> shellQueue = new LinkedBlockingQueue<Shell>();

	public void enqueueShell(Shell shell) {
		
		shellQueue.add(shell);
		
	}
	
	public void update(double dt) {
		shellParticleSimulator.update(shells, dt);
	}

	public void render() {
		
		for(int i = 0; i < shellQueue.size(); ++i) {
			shells.add(shellQueue.poll());
		}
		

		CompatibleShellRenderer.render(shells);

	}

}