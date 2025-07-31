package server_cmd;

import java.sql.Connection;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.BiFunction;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import server_db.Reservations;
import server_db.ReservationsDAO;
import server_db.Users;
import server_db.UsersDAO;
import server_manager.SocketBinder;
import server_util.Message;

public class Command {

	static final JSONParser parser = new JSONParser();
	private static ConcurrentHashMap<String, BiFunction<String, SocketBinder, String>> commandMapper
		= new ConcurrentHashMap<>();
	
	private static void registerCommand(String cmdName, BiFunction<String, SocketBinder, String> function){
		commandMapper.put(cmdName, function);
	}
	
	public static ConcurrentHashMap<String, BiFunction<String, SocketBinder, String>> getCommandMapper(){
		return commandMapper;
	}
	
	static {
		registerCommand("login", AuthCommand::login);
		registerCommand("logout", AuthCommand::logout);
		registerCommand("check", AuthCommand::check);
		registerCommand("signUp", AuthCommand::signUp);
		registerCommand("findID", AuthCommand::findID);
		registerCommand("findPWD", AuthCommand::findPWD);
		registerCommand("UpdatePWD", AuthCommand::UpdatePWD);
		registerCommand("my_reservation_print", AuthCommand::my_reservation_print);
		registerCommand("my_past_reservation_print", AuthCommand::my_past_reservation_print);
		registerCommand("findRoom", AuthCommand::findRoom);
		registerCommand("findUser", AuthCommand::findUser);
		registerCommand("changeInfo", AuthCommand::changeInfo);
		registerCommand("findRoomByid", AuthCommand::findRoomByid);
		registerCommand("reservation", AuthCommand::reservation);
		registerCommand("available_time", AuthCommand::available_time);
		registerCommand("deleteReservation", AuthCommand::deleteReservation);
		
		registerCommand("getRoomImg",FileTransferCommand::getRoomImg);
		registerCommand("setRoom",FileTransferCommand::setRoom);
		
		registerCommand("getRoomSchedules",ManagerCommand::getRoomSchedules);
		registerCommand("getUsers", ManagerCommand::getUsers);
		registerCommand("insertUser", ManagerCommand::insertUser);
		registerCommand("deleteUser", ManagerCommand::deleteUser);
		registerCommand("showRoom",ManagerCommand::showRoom);
		registerCommand("deleteRoom", ManagerCommand::deleteRoom);
		registerCommand("updateRoom", ManagerCommand::updateRoom);
	}
}