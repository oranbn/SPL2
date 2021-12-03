package bgu.spl.mics.application.services;

import bgu.spl.mics.MicroService;

/**
 * GPU service is responsible for handling the
 * {@link TrainModelEvent} and {@link TestModelEvent},
 * in addition to sending the {@link DataPreProcessEvent}.
 * This class may not hold references for objects which it is not responsible for.
 *
 * You can add private fields and public methods to this class.
 * You MAY change constructor signatures and even add new public constructors.
 */
public class GPUService extends MicroService {

    public GPUService(String name) {
        super("Change_This_Name");
        // TODO Implement this
    }

    @Override
    protected void initialize() {
        // TODO Implement this

    }
}
