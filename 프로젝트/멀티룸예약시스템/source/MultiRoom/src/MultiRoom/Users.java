package MultiRoom;

public class Users {
	
	private String id;
	private String password;
	private String email;
	private String name;
	private String user_type;

	public Users(String id, String password, String email,String name,String user_type) {
		this.id = id;
		this.password=password;
		this.email = email;
		this.name=name;
		this.user_type=user_type;
	}
	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public String getUser_type() {
		return user_type;
	}
	public void setUser_type(String usesr_type) {
		this.user_type = usesr_type;
	}

	@Override
	public String toString() {
		String text = String.format("id : %s, password : %s, email : %s, name:%s user_type: %s",
				id, password, email,name,user_type);
		return text;
	}
	
}
