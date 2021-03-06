package de.swagner.paxbritannica.frigate;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.Vector2;

import de.swagner.paxbritannica.Bullet;
import de.swagner.paxbritannica.Resources;

public class Missile extends Bullet {

	private MissileAI ai = new MissileAI(this);
	
	public Missile(int id, int team, Vector2 position, Vector2 facing) {
		super(id, team, position, facing);
		turnSpeed = 300f;
		accel = 300.0f;	
		bulletSpeed = 50;
		this.velocity = new Vector2().set(facing).scl(bulletSpeed);
		damage = 50;
		
		this.set(Resources.getInstance().missile);
		this.setOrigin(this.getWidth()/2, this.getHeight()/2);
	}
	
	@Override
	public void draw(Batch batch) {
		ai.update();
		
		super.draw(batch);
	}
}
