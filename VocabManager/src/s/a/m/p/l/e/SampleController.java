package s.a.m.p.l.e;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

@Path("sample_controller")
public class SampleController {

	@GET
	@Path("/gets")
	@Produces(MediaType.TEXT_PLAIN)
	public String Oyeah() {
		return "Oyeah";
	}
}
