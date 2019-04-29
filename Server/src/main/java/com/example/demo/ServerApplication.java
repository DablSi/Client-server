package com.example.demo;


import java.util.*;

import javax.servlet.annotation.MultipartConfig;
import javax.servlet.http.HttpServlet;

import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.web.bind.annotation.*;


@EnableAutoConfiguration
@RestController
public class ServerApplication extends HttpServlet {

    HashMap<String, Integer> devices;
    HashMap<Integer, Long> rooms;

    public ServerApplication() {
        super();
        devices = new HashMap<>();
        rooms = new HashMap<>();
    }

    public String deleteChar(String str) {
        if (str != null && str.length() > 0) {
            if (str.charAt(str.length() - 1) == '"')
                str = str.substring(0, str.length() - 1);
            if (str.charAt(0) == '"')
                str = str.substring(1);
        }
        return str;
    }

    @RequestMapping(value = "/post", method = RequestMethod.POST)
    public Void put(@RequestPart String device, Integer room, Long date) {
        device = deleteChar(device);
        devices.put(device, room);
        if (date != null || !rooms.containsKey(room)) {
            rooms.put(room, date);
        }
        System.out.println("Получено " + device + " в комнате " + room);
        return null;
    }

    @RequestMapping("/get/{device}")
    public Long get(@PathVariable("device") String device) {
        if (devices.containsKey(device)) {
            return rooms.get(devices.get(device));
        }
        return null;
    }

}