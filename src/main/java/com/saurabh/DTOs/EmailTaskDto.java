package com.saurabh.DTOs;

import java.io.Serializable;

public record EmailTaskDto(
        String toEmail,
        String subject,
        String body,
        String type
) implements Serializable {}
