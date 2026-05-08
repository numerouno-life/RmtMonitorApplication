package ru.practicum.modbus;

import lombok.extern.slf4j.Slf4j;
import net.wimpi.modbus.io.ModbusTCPTransaction;
import net.wimpi.modbus.msg.ReadInputRegistersRequest;
import net.wimpi.modbus.msg.ReadInputRegistersResponse;
import net.wimpi.modbus.net.TCPMasterConnection;

import java.io.IOException;
import java.net.InetAddress;

@Slf4j
public class ModbusTcpClient implements ModbusClient {

    private final String ip;
    private final int port;
    private final int unitId;

    public ModbusTcpClient(String ip, int port, int unitId) {
        this.ip = ip;
        this.port = port;
        this.unitId = unitId;
    }

    private double readRegister(int address) throws IOException {
        try {
            InetAddress addr = InetAddress.getByName(ip);
            TCPMasterConnection con = new TCPMasterConnection(addr);
            con.setPort(port);
            con.connect();

            ReadInputRegistersRequest req = new ReadInputRegistersRequest(address, 1);
            req.setUnitID(unitId);

            ModbusTCPTransaction trans = new ModbusTCPTransaction(con);
            trans.setRequest(req);
            trans.execute();

            ReadInputRegistersResponse res = (ReadInputRegistersResponse) trans.getResponse();
            con.close();

            return res.getRegisterValue(0);
        } catch (Exception e) {
            throw new IOException("Ошибка Modbus TCP", e);
        }
    }

    @Override
    public double[] readTemperatures(int frontRegister, int rearRegister) {
        Double front = null;
        Double rear = null;

        try {
            front = readRegister(frontRegister);
        } catch (IOException e) {
            log.error("Ошибка чтения температуры переднего подшипника", e);
        }

        try {
            rear = readRegister(rearRegister);
        } catch (IOException e) {
            log.error("Ошибка чтения температуры заднего подшипника", e);
        }
        return new double[]{
                front != null ? front : Double.NaN,
                rear != null ? rear : Double.NaN
        };
    }
}
