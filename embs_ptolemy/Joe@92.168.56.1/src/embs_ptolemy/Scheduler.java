package embs_ptolemy;

import ptolemy.actor.*;
import ptolemy.data.IntToken;
import ptolemy.data.Token;
import ptolemy.data.expr.*;
import ptolemy.kernel.*;
import ptolemy.kernel.util.*;

public class Scheduler extends TypedAtomicActor{
	protected TypedIOPort input;
	protected TypedIOPort[] outputs;
	protected int num_outputs;
	protected TypedIOPort trig_out;
	protected TypedIOPort desired_processor;
	protected Token token_storage;
	
	public Scheduler(CompositeEntity container, String name) throws NameDuplicationException, IllegalActionException {
		super(container, name);
		
		input = new TypedIOPort(this, "input", true, false);
		num_outputs = 5;
		outputs = new TypedIOPort[num_outputs];
		for(int i = 0; i < num_outputs; i++) {
			outputs[i] = new TypedIOPort(this, "output" + Integer.toString(i), false, true);
		}
		trig_out = new TypedIOPort(this, "trigger out", false, true);
		desired_processor = new TypedIOPort(this, "desired processor", true, false);
	}
	public void initialize() {
		
	}
	
	public void fire() throws IllegalActionException {
		if(input.hasToken(0)) {
			Token t = input.get(0);
			token_storage = t;
			trig_out.send(0, new Token());
		}
		if(desired_processor.hasToken(0)) {
			Token p = desired_processor.get(0);
			int processor = IntToken.convert(p).intValue();
			outputs[processor].send(0, token_storage);
		}
	}
}
