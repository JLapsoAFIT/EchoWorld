package Boid.behaviors;

import Boid.BoidState;
import Vehicles.SensedObject;
import Vehicles.State;
import behaviorFramework.Action;
import behaviorFramework.Behavior;
import org.dyn4j.geometry.Vector2;

import java.util.List;
import java.util.Map;

/**
 * <p>Causes the vehicle to veer away from obstacles.</p>
 */
public class BoidAlign extends Behavior {

    /**
     * Activation threshold (distance < DISTANCE_LIMIT)
     */
    private final int DISTANCE_LIMIT = 3;

    // Vote = 1
    // Motor outs are 0.8 and 0.05

    /**
     * <p>Checks for boids in neighborhood and matches speed and angle.<br>
     * Action outputs are for steer and thrust.</p>
     *
     * <p>Vote = 1 if vehicle is has a neighbor.<br>
     * Vote = 0 otherwise</p>
     *
     *
     * @param state current vehicle state
     * @return an action to turn away from obstacles
     */
    public Action genAction(State state) {
        assert (state != null);

        Action action = new Action();

        action.name = new String("BoidAlign");
        action.setVote(0);

        Vector2 alignedAngle = new Vector2();

        // Iterate through our Neighbors
        int count = ((BoidState)state).neighborList.size();
        for(Map.Entry<Integer, BoidState.Neighbor> neighbor: ((BoidState)state).neighborList.entrySet() ) {
            Vector2 nVec = neighbor.getValue().velocity;
            alignedAngle.dot(nVec);
        }
        double thrust = state.getVelocity().getMagnitude();
        double steer = state.getAngularVelocity();
        if(count > 0) {
            alignedAngle.divide(count);
            thrust = alignedAngle.getMagnitude();
            steer = alignedAngle.getAngleBetween(state.getVelocity());
            action.setVote(1);
        }

        action.setRightWheelVelocity(thrust);
        action.setLeftWheelVelocity(steer);

        return action;
    }
}
