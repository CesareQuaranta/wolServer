package wol.server.dom;

import java.net.URL;

import wol.dom.iEvent;

public class BackgroundChange implements iEvent {
	private static final long serialVersionUID = -3229218063263929351L;
	private URL url;
	public BackgroundChange(URL url) {
		this.url=url;
	}
	public URL getUrl() {
		return url;
	}

}
