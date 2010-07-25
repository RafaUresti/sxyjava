import java.util.*;
public class Plex {
    Set<Plex> containers   = new HashSet<Plex>();
    Set<Plex> contents      = new HashSet<Plex>();
    Set<Plex> origins         = new HashSet<Plex>();
    Set<Plex> destinations = new HashSet<Plex>();
    public Object value;
    
    protected Plex() { }
    
    protected Plex(Object value) {
        this.value = value;
    }
    void addContainer(Plex that) {
        this.containers.add(that);
        that.contents.add(this);
//        ArrayList<?> stuff = new ArrayList<?>();
        how1();
    }

    private void how1() {
		// TODO Auto-generated method stub
		
	}

	void removeContainer(Plex that) {
        this.containers.remove(that);
        that.contents.remove(this);
    }

}
