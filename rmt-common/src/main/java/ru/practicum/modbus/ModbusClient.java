package ru.practicum.modbus;

import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public interface ModbusClient {
    double[] readTemperatures(int frontRegister, int rearRegister) throws IOException;
}