import java.time.Clock;
import java.util.ArrayList;

import ptolemy.actor.*;
import ptolemy.data.IntToken;
import ptolemy.data.Token;
import ptolemy.data.expr.*;
import ptolemy.kernel.*;
import ptolemy.kernel.util.*;
import ptolemy.data.IntToken.*;
import ptolemy.actor.util.Time;

public class Processor extends TypedAtomicActor{
	protected TypedIOPort input;
	protected TypedIOPort output;
	protected TypedIOPort util;
	protected Boolean busy;
	protected long busyUntil;
	protected Director dir;
	protected ArrayList<Token> queue;
	
	public Processor(CompositeEntity container, String name) throws NameDuplicationException, IllegalActionException {
		super(container, name);
		
		input = new TypedIOPort(this, "input", true, false);
		output = new TypedIOPort(this, "output", false, true);
		util = new TypedIOPort(this, "utilisation", false, true);
		busy = false;
		dir = this.getDirector();
		busyUntil = 0;
		queue = new ArrayList<Token>();
	}
	
	public void initialize() {
		busy = false;
		busyUntil = 0;
		dir = this.getDirector();
		queue = new ArrayList<Token>();
	}
	
	public void fire() throws IllegalActionException{	
		Token u, q = new Token();
		Boolean hasToken = true;
		try {
			q = input.get(0);
			queue.add(q); // Add incoming token to queue
		} catch (NoTokenException e) {
			hasToken = false;
		} // In case actor has asked director to fire at specific time.		
		double currentTime = dir.getModelTime().getDoubleValue();
		if(currentTime >= busyUntil) busy = false;
		if(!busy && queue.size() != 0) {
			Token t = queue.remove(0); // Get head of queue
			IntToken intT = IntToken.convert(t);	
			long milliseconds = intT.longValue();
			busyUntil = Math.round(currentTime) + milliseconds;
			busy = true;
			u = new IntToken(100);
			dir.fireAt(this, busyUntil);
		} else {
			u = new IntToken(0);
			if (hasToken) output.send(0,q);
		}
		System.out.println(currentTime + ", " + busyUntil + ", queue length: " + queue.size());
		util.send(0, u);
	}
}
