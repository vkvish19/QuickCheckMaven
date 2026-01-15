package com.vkvish19.github.util;

import com.google.common.hash.Hashing;

import java.nio.charset.StandardCharsets;

public class JsessionIdCorrelationUtil
{
    public static void main(String[] args)
    {
        args = new String[] {"469D605EF6A03CE029B906B85E81EA09"};
        String sessionCorrelationId = Hashing.sha256().hashString(args[0], StandardCharsets.UTF_8).toString();

        System.out.println("Session ID: " + args[0]);
        System.out.println("sessionCorrelationId = " + sessionCorrelationId);
    }
}
