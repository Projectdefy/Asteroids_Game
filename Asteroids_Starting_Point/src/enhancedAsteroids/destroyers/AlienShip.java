package enhancedAsteroids.destroyers;

import java.awt.Shape;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.*;

import javax.swing.Timer;

import enhancedAsteroids.Constants;
import enhancedAsteroids.EnhancedController;
import enhancedAsteroids.Participant;
import enhancedAsteroids.participants.Debris;
import enhancedAsteroids.participants.EnemyBullet;
import enhancedAsteroids.participants.Ship;

public class AlienShip extends Participant implements ShipDestroyer, ActionListener {

	//point value
	private int worth = 200;
	private EnhancedController controller;
	private Shape outline;
	private Timer pathChanger;
	private Timer firingTimer;
	private boolean leftOrRight;
	
	/**
	 * The Class that manages the alien ship participants.
	 */
	public AlienShip(boolean isSmall, EnhancedController controller){
		
		leftOrRight = Constants.RANDOM.nextBoolean();
		worth += (isSmall ? 800: 0);
		this.controller = controller;
		outline = createAlienShipOutline(isSmall);
		pathChanger = new  Timer(1000, this);
		firingTimer = new  Timer(2000, this);
		setPosition((Constants.RANDOM.nextBoolean() ? 0 : Constants.SIZE), 25 + Constants.RANDOM.nextInt(700));
		setRotation(Math.PI);
		pathChanger.start();
		firingTimer.start();
		playShipClip(true);
	}
	private void playShipClip(boolean onOff){
		if(worth == 1000 && onOff ){
			getSounds().playSaucerSmallClip();;
		}else if(worth == 1000 && !onOff){
			getSounds().stopSaucerSmallClip();
		}else if(onOff){
			getSounds().playSaucerBigClip();
		}else{
			getSounds().stopSaucerBigClip();
		}
	}
	private double[] alienVelocity(boolean lorR){
		double speed = Constants.ALIENSHIP_SPEED[1];
		if (worth == 1000)
		{
			speed = Constants.ALIENSHIP_SPEED[0];
		}
		;
		double direction = (lorR ? Math.PI : 2*Math.PI);
		
		switch(Constants.RANDOM.nextInt(3)){
		case 0:
			direction += 1;
			break;
		case 1:
			direction -= 1;
			break;		
		default:
			break;
		}
		return new double[]{speed,direction};
	}
	
	private Shape createAlienShipOutline(boolean isSmall) {
		Path2D.Double poly = new Path2D.Double();
		poly.moveTo(-9, 0);
		poly.lineTo(-4, -4);
		poly.lineTo(4, -4);
		poly.lineTo(9, 0);
		poly.lineTo(-9, 0);
		poly.lineTo(-4, 4);
		poly.lineTo(-2, 8);
		poly.lineTo(2, 8);
		poly.lineTo(4, 4);
		poly.lineTo(-4,4);
		poly.lineTo(4 ,4);
		poly.lineTo(9,0);
		poly.closePath();
		poly.transform(AffineTransform.getScaleInstance(1.1, 1.1));
		if(!isSmall){
		poly.transform(AffineTransform.getScaleInstance(2, 2));
		}
		return poly;
	}
	@Override
	protected Shape getOutline() {
		return outline;
	}

	@Override
	public void collidedWith(Participant p) {
		if(p instanceof AsteroidDestroyer && !(p instanceof EnemyBullet)){
			playShipClip(false);
			getSounds().playBangAlienShipClip();
			controller.etGoneHome(worth);
			Participant.expire(this);
			controller.addParticipant(new Debris(this.getX(), this.getY(), 15));
            controller.addParticipant(new Debris(this.getX(), this.getY(), 13));
            controller.addParticipant(new Debris(this.getX(), this.getY(), 7));
            controller.addParticipant(new Debris(this.getX(), this.getY(), 5));
            controller.addParticipant(new Debris(this.getX(), this.getY(), 10));
		}
		if(p instanceof ShipDestroyer && !(p instanceof EnemyBullet)){
			playShipClip(false);
			getSounds().playBangAlienShipClip();
			controller.etGoneHome(0);
			Participant.expire(this);
			controller.addParticipant(new Debris(this.getX(), this.getY(), 15));
            controller.addParticipant(new Debris(this.getX(), this.getY(), 13));
            controller.addParticipant(new Debris(this.getX(), this.getY(), 7));
            controller.addParticipant(new Debris(this.getX(), this.getY(), 5));
            controller.addParticipant(new Debris(this.getX(), this.getY(), 10));
		}
		
	}
	@Override
	public void actionPerformed(ActionEvent arg0) {
		if(!this.isExpired())
		{
			if(arg0.getSource() == pathChanger){
				double[] velo = alienVelocity(leftOrRight);
				this.setVelocity(velo[0], velo[1]);
			}else if(arg0.getSource() == firingTimer){
				double shipdir;
				if(controller.getShip() == null)
				{
					shipdir = Math.PI;
				}
				else
				{
				double shipX = controller.getShip().getX() - this.getX();
				double shipY = controller.getShip().getY() - this.getY();
				shipdir =  Math.atan2(shipY, shipX);
				}
				EnemyBullet bullet = new EnemyBullet (this.getX(), this.getY(), (worth == 1000 ? shipdir  : (Math.PI*2)/(1+Constants.RANDOM.nextInt(10))));
				controller.addParticipant(bullet);
				Ship.getSounds().playFireClip();
			}
		}
		else
		{
			pathChanger.stop();
			firingTimer.stop();
		}
		}


}