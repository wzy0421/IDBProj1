package datatypes;
import java.util.List;

public class CreateEvent {
	public CreateEvent(String facebookId, String ename, String detail,
			List<CreatePlan> plans) {
		this.facebookId = facebookId;
		this.ename = ename;
		this.detail = detail;
		this.plans = plans;
	}

	public String facebookId;
	public String ename;
	public String detail;
	public List<CreatePlan> plans;
}