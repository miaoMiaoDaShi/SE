package lq.xxp.se.Bean;

//"id":"27",
//"name": "加勒比-.不认识这货",
//"time": "2016-10-20 9:34:21",
//"link": "Xtq7w5DBuUY6h54Gotrmf2hDXgWGXZO/o0Kc4pw7vgb/u/Y6hy==",
//"imgUrl": "http://img.qququ11.com/pic/2121k/OB74Q6f.jpg",
//"type":"无码"
public class PlayBean {
	String id;
	String name;
	String time;
	String link;
	String imgUrl;
	String type;
	
	
	public PlayBean(String id, String name, String time, String link, String imgUrl, String type) {
		this.id = id;
		this.name = name;
		this.time = time;
		this.link = link;
		this.imgUrl = imgUrl;
		this.type = type;
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getTime() {
		return time;
	}
	public void setTime(String time) {
		this.time = time;
	}
	public String getLink() {
		return link;
	}
	public void setLink(String link) {
		this.link = link;
	}
	public String getImgUrl() {
		return imgUrl;
	}
	public void setImgUrl(String imgUrl) {
		this.imgUrl = imgUrl;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}

	
}
