package com.saucesum.mc.saucebukkit;

import java.util.HashMap;
import java.util.Map;

import com.google.gson.Gson;

public class Emit {
	private static Gson gson = new Gson();

	private String cmd;
	private Map<String, String> data;

	public Emit(String cmd){
		this.cmd = cmd;
		this.data = new HashMap<String, String>();
	}

	@Override
	public String toString() {
		return "Emit[" + cmd + ", " + data + "]";
	}

	public void put(String key, String value) {
		data.put(key, value);
	}

	public String get(String key) {
		return data.get(key);
	}

	public String getType() {
		return cmd;
	}

	public static Emit newSay(String chan, String msg) {
		Emit e = new Emit("say");
		e.put("chan", chan);
		e.put("msg", msg);
		return e;
	}

	public static Emit newMsg(String chan, String user, String msg) {
		Emit e = new Emit("msg");
		e.put("chan", chan);
		e.put("user", user);
		e.put("msg", msg);
		return e;
	}

	public String toJson() {
		return gson.toJson(this);
	}

	public static Emit fromJson(String json) {
		return gson.fromJson(json, Emit.class);
	}
}