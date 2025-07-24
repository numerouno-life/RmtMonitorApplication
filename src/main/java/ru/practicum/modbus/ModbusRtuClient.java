package ru.practicum.modbus;

import net.wimpi.modbus.io.ModbusSerialTransaction;
import net.wimpi.modbus.msg.ReadInputRegistersRequest;
import net.wimpi.modbus.msg.ReadInputRegistersResponse;
import net.wimpi.modbus.net.SerialConnection;
import net.wimpi.modbus.util.SerialParameters;

import java.io.IOException;

public class ModbusRtuClient implements ModbusClient {

    private final String portName;
    private final int unitId;
    private final SerialParameters params;

    public ModbusRtuClient(String portName, int unitId) {
        this.portName = portName;
        this.unitId = unitId;
        this.params = new SerialParameters();
        params.setPortName(portName);
        params.setBaudRate(9600);
        params.setDatabits(8);
        params.setParity("even");
        params.setStopbits(1);
        params.setEncoding("rtu");
        params.setEcho(false);
    }

    private double readRegister(int registerAddress) throws IOException {
        SerialConnection con = new SerialConnection(params);

        try {
            con.open();

            ReadInputRegistersRequest req = new ReadInputRegistersRequest(registerAddress, 1);
            req.setUnitID(unitId);

            ModbusSerialTransaction trans = new ModbusSerialTransaction(con);
            trans.setRequest(req);
            trans.execute();

            ReadInputRegistersResponse res = (ReadInputRegistersResponse) trans.getResponse();
            return res.getRegisterValue(0);
        } catch (Exception e) {
            throw new IOException("Ошибка чтения из порта " + portName, e);
        } finally {
            if (con.isOpen()) {
                con.close();
            }
        }
    }

    @Override
    public double[] readTemperatures(int frontRegister, int rearRegister) throws IOException {
        double front = readRegister(frontRegister);
        double rear = readRegister(rearRegister);
        return new double[]{front, rear};
    }
}
