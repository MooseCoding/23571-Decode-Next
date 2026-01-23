package org.firstinspires.ftc.teamcode.wayfinder;

/*
 * This class is a simple PD loop which calculates an output power from an input and coefficients.
 */
public class PIDLoop{
    private double previousError;
    private double previousTime;
    private double previousOutput;

    /**
     * The meat and potatoes of this class, calculate the desired power for a an error with gains
     * and maximum acceleration.
     * @param error Difference between current position and target position.
     * @param pGain The P Gain.
     * @param dGain The D Gain.
     * @param accel the maximum change in motor power allowed per second. A value of 10 allows the
     *             motor power to slew from 0 to 1 in 0.1s.
     * @param currentTime the current time in seconds.
     * @return the target power to apply from the PID loop.
     */
    public double calculateAxisPID(double error, double pGain, double dGain, double accel, double currentTime){
        double p = error * pGain;
        double cycleTime = currentTime - previousTime;
        double d = dGain * (previousError - error) / (cycleTime);
        double output = p + d;
        double dV = cycleTime * accel;

        double max = Math.abs(output);
        if(max > 1.0){
            output /= max;
        }

        if((output - previousOutput) > dV){
            output = previousOutput + dV;
        } else if ((output - previousOutput) < -dV){
            output = previousOutput - dV;
        }

        previousOutput = output;
        previousError  = error;
        previousTime   = currentTime;

        return output;
    }
}