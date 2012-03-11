package scripts.itsm.assignee;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Random;


public class RandomAssignee implements PeekAssigneeStrategy{
    protected ArrayList candidates;

    
    public RandomAssignee(ArrayList candidates){
     this.candidates = candidates;
    
    }
    public Object peek() {
        Random r = new Random(Calendar.getInstance().getTimeInMillis());
        return candidates.get(r.nextInt(candidates.size()));
    }
}
