package mychat3.server;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class ClientChatListener implements ActionListener{
	ClientChatView view;

	public ClientChatListener(ClientChatView view) {
		super();
		this.view = view;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if(e.getSource()==view.txtinput ||
				e.getSource()==view.btnsend){
			view.sendMsg("chatting/"+
					view.txtinput.getText().trim()
					+"/"+view.nickname);
			view.txtinput.setText("");
		}
		
	}
	
	
}
