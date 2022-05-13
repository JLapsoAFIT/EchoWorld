package Braitenburg;

import framework.SimulationBody;
import org.dyn4j.dynamics.BodyFixture;
import org.dyn4j.geometry.*;
import org.dyn4j.world.DetectFilter;
import org.dyn4j.world.World;
import org.dyn4j.world.result.RaycastResult;

import java.awt.*;
import java.awt.geom.Ellipse2D;
import java.util.List;

/**
 * Basic vehicle class.
 *
 * David King
 * Started:  8 Apr 2022
 * Last Update:  20 Apr
 */

public class Vehicle extends SimulationBody {
    // Basic vehicle build
    private SimulationBody baseVehicle;

    private final Vector2 leftWheelLocation = new Vector2(-0.5, -0.5);
    private final Vector2 rightWheelLocation = new Vector2( 0.5, -0.5);

    private final double MAX_VELOCITY = 2; // arbitrary right now
    private final int MAX_TORQUE = 1; // how fast we can turn
    private final int SENSOR_RANGE = 20; // how far the line casts go
    private final int TOLERANCE = 0; // how far "off" the target can be, allows us to home in on a target

    private State state;

    // array of values to "sweep" across -- hand jammed to get a reasonable sweep that doesn't eat too much processing time
    double[] sweepValues = {
            -0.0872, -0.0654, -0.0436, -0.0348, -0.0261, -0.0174, -0.0087, 0.0000, 0.0001,
             0.0010,  0.0020,  0.0040,  0.0060,  0.0080,  0.0100,  0.0110,  0.0120, 0.0140, 0.0160,
             0.0180,  0.0200,  0.0220,  0.2400,  0.2600,  0.2800,  0.3000,  0.3200, 0.3400, 0.3600,
             0.3800,  0.4000,  0.4200,  0.4400,  0.4600,  0.4800,  0.5000,  0.0520, 0.0540, 0.5600,
             0.5800,  0.6000,  0.6200,  0.6400,  0.6600,  0.6800,  0.7000,  0.7200, 0.7400, 0.7600};
            // Added 7: -10. -7.5, -5, -4, -3, -2, -1
            // Cut 12:  , 0.780, 0.800, 0.820, 0.0840, 0.0860, 0.880, 0.900, 0.920, 0.940, 0.960, 0.980, 1.00};

    // Creates the world
    protected World<SimulationBody> myWorld;

    public Vehicle() {
    }

    private void bulkInit(World<SimulationBody> myWorld) {
        this.myWorld = myWorld;

        // Create our vehicle
        baseVehicle = new SimulationBody();
        baseVehicle.addFixture(Geometry.createRectangle(1.0, 1.5));
        baseVehicle.addFixture(Geometry.createCircle(0.35));
        baseVehicle.setMass(MassType.NORMAL);
        myWorld.addBody(baseVehicle);

        // -- grabbers
        Convex leftGrabber = Geometry.createRectangle(.1, .2);
        Convex rightGrabber = Geometry.createRectangle(.1, .2);
        leftGrabber.translate(-.25,.8);
        rightGrabber.translate(.25, .8);

        // -- sensors
        Convex leftSensor = Geometry.createCircle(0.1);
        Convex rightSensor = Geometry.createCircle(0.1);
        leftSensor.translate(-.50,.8);
        rightSensor.translate(.50, .8);

        // -- "wheels"
        Convex leftWheel = Geometry.createRectangle(.33, .5);
        Convex rightWheel = Geometry.createRectangle(.33, .5);
        // if we add these as-is to the body they will both be positioned at the center, so we have to offset them
        leftWheel.translate(leftWheelLocation);
        rightWheel.translate(rightWheelLocation);

        // add to the vehicle -- note: these are like the portholes on a '57 chevy, they are just for looks
        baseVehicle.addFixture(leftGrabber);
        baseVehicle.addFixture(rightGrabber);
        baseVehicle.addFixture(leftSensor);
        baseVehicle.addFixture(rightSensor);
        baseVehicle.addFixture(leftWheel);
        baseVehicle.addFixture(rightWheel);
        baseVehicle.setColor(Color.CYAN);

/*        // -- "wheels"
        leftWheel = new SimulationBody();
        leftWheel.addFixture(Geometry.createRectangle(.33, .5));
        rightWheel = new SimulationBody();
        rightWheel.addFixture(Geometry.createRectangle(.33, .5));
        // if we add these as-is to the body they will both be positioned at the center, so we have to offset them
        leftWheel.translate(-.5,-.50);
        rightWheel.translate(.5, -.50);
        // Set their mass to be light so that they move with the vehicle
        leftWheel.setMass(MassType.NORMAL);
        rightWheel.setMass(MassType.NORMAL);

        myWorld.addBody(leftWheel);
        myWorld.addBody(rightWheel);

        // Add weld joints between wheels and body so that everything moves together
        WeldJoint<SimulationBody> lw = new WeldJoint<>(baseVehicle, leftWheel, leftWheelLocation);
        this.myWorld.addJoint(lw);

        WeldJoint<SimulationBody> rw = new WeldJoint<>(baseVehicle, rightWheel, rightWheelLocation);
        this.myWorld.addJoint(rw);
*/
        //Random rand = new Random();
        int max = 15;
        int min = -15;
        baseVehicle.translate(Math.floor(Math.random()*(max-min+1)+min),Math.floor(Math.random()*(max-min+1)+min));
        baseVehicle.setUserData("Vehicle");
    }

    public void initialize(World<SimulationBody> myWorld) {
        bulkInit(myWorld);
        state = new State();
    }

    public void initialize(World<SimulationBody> myWorld, State s) {
       // baseVehicle.setMass(new Mass(baseVehicle.getWorldCenter(),0.5,0.5)); // work in progress
        bulkInit(myWorld);
        state = s;
    }

    /**
     *
     */
    public boolean sense() {
        state.tick();
        // Following code block draws Rays out from each sensor and stores data into objectsDetected
        rayCasting(-0.50, 0.8, 0); // left sensor
        rayCasting(0.50, 0.8, 1); // right sensor

        state.setVelocity(baseVehicle.getLinearVelocity()); // LinearVelocity captures heading and speed

        return true;
    }

    /**
     * Must be overloaded.
     * Called before render to show action in the current time step.
     * Current action is no op
     */
    public Action decideAction() { //Graphics2D g) {
        Action result = new Action();

        return result;
    }

    /**
     * Called from render.  Must provide it the coordinates of the specific sensor you want to ray cast
     * from. -- still very much a work in progress.
     *
     * @param sensor_x
     * @param sensor_y
     * @param sensor_dir
     */
    private void rayCasting(double sensor_x, double sensor_y, int sensor_dir) {
        final double length = SENSOR_RANGE;

        Vector2 start = baseVehicle.getTransform().getTransformed(new Vector2(sensor_x, sensor_y));

        double x = 0.0;
        double y = 0.01;
        SensedObject obj;
        for(double i : sweepValues) { //sweepValues) {
            // +- i is determined by directionality... so, we will add that as a temp variable until I discuss
            // some of these choices with Bert.
            double j = i;
            if(sensor_dir < 1) {
                j = i * -1;
            }
            Vector2 direction = baseVehicle.getTransform().getTransformedR(new Vector2(x + j, y - i));
            Ray ray = new Ray(start, direction);

            List<RaycastResult<SimulationBody, BodyFixture>> results = myWorld.raycast(ray, length, new DetectFilter<SimulationBody, BodyFixture>(true, true, null));
            for (RaycastResult<SimulationBody, BodyFixture> result : results) {
                // Get the direction between the center of the vehicle and the impact point
                Vector2 heading = new Vector2(baseVehicle.getWorldCenter(), result.getRaycast().getPoint());
                double angle = heading.getAngleBetween(baseVehicle.getLinearVelocity()); // radians
                double distance = result.getRaycast().getDistance();
                String type = "UNKNOWN";
                if (result.getBody().getUserData() != null) { // If not set, just make Unknown
                    if (result.getBody().getUserData().equals("Light")) {
                        type = "Light";
                    } else {
                        type = "Obstacle";
                    }
                }

                String side = "Left";
                if (sensor_x > 0.0) {
                    side = "Right";
                }

                obj = new SensedObject(heading, angle, distance, type, side, result.getRaycast().getPoint());
                state.addSensedObject(obj);
            }
        }
    }

    @Override
    public void render(Graphics2D g, double scale) {
        super.render(g, scale);

        // draw the sensor intersections
        final double r = 4.0;
        final double rayScale = 20;//48; // <-- this has to match the world scale, otherwise you get wonky results
        List<SensedObject> sensedObjects = state.getSensedObjects();
        for (SensedObject obj : sensedObjects) {
                Vector2 point = obj.getHit(); //result.getRaycast().getPoint();
                g.setColor(Color.GREEN);
                g.fill(new Ellipse2D.Double(
                        point.x * rayScale - r * 0.5,
                        point.y * rayScale - r * 0.5,
                        r,
                        r));
        }
    }

    /**
     *  Applies the action to the vehicle. Calculates the torque and force from the left and right wheel velocity
     *  requests and then clamps them into a performance range.
     *
     * @param a Action being applied to the vehicle
     */
    public void act(Action a) {
        double left = a.getLeftWheelVelocity();
        double right = a.getRightWheelVelocity();

        state.setLeftWheelVelocity(left);
        state.setRightWheelVelocity(right);

        // Calculate Torque
        Vector2 applyLeft = new Vector2(0.0, 1.0);
        applyLeft.multiply(left);
        Vector2 applyRight = new Vector2(0.0,1.0);
        applyRight.multiply(right);
        double torqueL = leftWheelLocation.cross(applyLeft);
        double torqueR = rightWheelLocation.cross(applyRight);

//        System.out.println("Left Torque: " + torqueL + " Right Torque: " + torqueR);

        // Apply torque to baseVehicle
        double baseTorque = torqueL + torqueR;
        baseVehicle.applyTorque(baseTorque);

        // Constrain the vehicle to prevent spinning out of control
        // Apply the forces in the direction the baseVehicle is facing
        Vector2 baseNormal = baseVehicle.getTransform().getTransformedR(new Vector2(0.0,1.0));
        baseNormal.multiply(left+right);
//        System.out.println(baseNormal);
        baseVehicle.setLinearVelocity(baseNormal);

        // Get the linear velocity in the direction of the baseVehicle's front
        Vector2 clamp = this.baseVehicle.getTransform().getTransformedR(new Vector2(0.0, 1.0));
        double defl = baseVehicle.getLinearVelocity().dot(clamp);

        // clamp the velocity
        defl = Interval.clamp(defl, 0.0, MAX_VELOCITY);
        baseVehicle.setLinearVelocity(baseNormal.multiply(defl));

        // clamp the angular velocity
        double av = baseVehicle.getAngularVelocity();
        av = Interval.clamp(av, -MAX_TORQUE, MAX_TORQUE);
        baseVehicle.setAngularVelocity(av);
    }

    /**
     * Set the color of the vehicle
     *
     * @param c Color
     */
    public void setColor(Color c) {
        baseVehicle.setColor(c);
    }

}







