package ajp;

import org.apache.coyote.ajp.AjpMessage;

import java.util.ArrayList;
import java.util.List;

public class TesterAjpMessage extends AjpMessage {

    private final List<Header> headers = new ArrayList<Header>();


    public TesterAjpMessage(int packetSize) {
        super(packetSize);
    }

    public byte readByte() {
        return buf[pos++];
    }

    public void addAttr(String key, String value){
        appendByte(0x0A);
        appendString(key);
        appendString(value);
    }

    public int readInt() {
        int val = (buf[pos-2] & 0xFF ) << 8;
        val += buf[pos-1] & 0xFF;
        pos += 3;
        return val;
    }

    public String readString() {
        int len = readInt();
        return readString(len-4);
    }

    public String readString(int len) {
        StringBuilder buffer = new StringBuilder(len);

        for (int i = 0; i < len; i++) {
            char c = (char) buf[pos++];
            buffer.append(c);
        }
        readByte();

        return buffer.toString();
    }

    public void addHeadInfo() {
        appendInt(headers.size());

        for (Header header : headers) {
            header.append(this);
        }
    }

    @Override
    public void end() {

        appendByte(0xFF);

        len = pos;
        int dLen = len - 4;

        buf[0] = (byte) 0x12;
        buf[1] = (byte) 0x34;
        buf[2] = (byte) ((dLen>>>8) & 0xFF);
        buf[3] = (byte) (dLen & 0xFF);
    }


    @Override
    public void reset() {
        super.reset();
        headers.clear();
    }


    private static class Header {
        private final int code;
        private final String name;
        private final String value;

        public Header(int code, String value) {
            this.code = code;
            this.name = null;
            this.value = value;
        }

        public Header(String name, String value) {
            this.code = 0;
            this.name = name;
            this.value = value;
        }

        public void append(TesterAjpMessage message) {
            if (code == 0) {
                message.appendString(name);
            } else {
                message.appendInt(code);
            }
            message.appendString(value);
        }
    }
}