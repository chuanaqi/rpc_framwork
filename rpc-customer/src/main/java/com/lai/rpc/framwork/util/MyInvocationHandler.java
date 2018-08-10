package com.lai.rpc.framwork.util;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.net.Socket;

/**
 * Created by laiweigeng on 2018/8/10.
 */
public class MyInvocationHandler implements InvocationHandler {
    private String host;
    private int port;

    public MyInvocationHandler(String host,int port) {
        this.host = host;
        this.port = port;
    }

    public Object invoke(Object proxy, Method method, Object[] arguments) throws Throwable {
        Socket socket = new Socket(host, port);
        try {
            ObjectOutputStream output = new ObjectOutputStream(socket.getOutputStream());
            try {
                output.writeUTF(method.getName());
                output.writeObject(method.getParameterTypes());
                output.writeObject(arguments);
                ObjectInputStream input = new ObjectInputStream(socket.getInputStream());
                try {
                    Object result = input.readObject();
                    if (result instanceof Throwable) {
                        throw (Throwable) result;
                    }
                    return result;
                } finally {
                    input.close();
                }
            } finally {
                output.close();
            }
        } finally {
            socket.close();
        }
    }


}
