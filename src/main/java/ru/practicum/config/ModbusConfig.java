package ru.practicum.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import ru.practicum.modbus.ModbusClient;
import ru.practicum.modbus.ModbusTcpClient;

@Configuration
public class ModbusConfig {

    @Bean
    public ModbusClient modbusClient() {
        return new ModbusTcpClient("192.168.0.100", 502, 1);
    }

/*
    @Bean
    public ModbusClient modbusClient() {
        return new ModbusRtuClient("/dev/ttyUSB0", 1);
    }
*/

}
