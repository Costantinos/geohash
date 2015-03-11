

import java.util.ArrayList;
import java.util.HashMap;

public class Geohash {

        private static int numbits = 5 * 5;
        final static char[] digits = { '0', '1', '2', '3', '4', '5', '6', '7', '8',
                        '9', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'j', 'k', 'm', 'n', 'p',
                        'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z' };
        
        final static HashMap<Character, Integer> lookup = new HashMap<Character, Integer>();
        static {
                int i = 0;
                for (char c : digits)
                        lookup.put(c, i++);
        }

        public static void main(String[] args) {
                Geohash e = new Geohash();
                String s = e.encode(21.33248519897461,-158.02630615234375);
                System.out.println(s);
        }

        public double[] decode(String geohash) {
                StringBuilder buffer = new StringBuilder();
                for (char c : geohash.toCharArray()) {

                        int i = lookup.get(c) + 32;
                        buffer.append( Integer.toString(i, 2).substring(1) );
                }
                
                ArrayList<Boolean> lonset = new  ArrayList<Boolean>(64);
                ArrayList<Boolean> latset = new  ArrayList<Boolean>(64);
                
                for (int i = 0; i < 64; i++) {
                	lonset.add(false);
                	latset.add(false);
				}
                
                //even bits
                int j =0;
                for (int i=0; i< numbits*2;i+=2) {
                        boolean isSet = false;
                        if ( i < buffer.length() )
                          isSet = buffer.charAt(i) == '1';
                        lonset.add(j++, isSet);
                }
                
                //odd bits
                j=0;
                for (int i=1; i< numbits*2;i+=2) {
                        boolean isSet = false;
                        if ( i < buffer.length() )
                          isSet = buffer.charAt(i) == '1';
                        latset.set(j++, isSet);
                }
                
                double lon = decode(lonset, -180, 180);
                double lat = decode(latset, -90, 90);
                
                return new double[] {lat, lon};         
        }
        
        private double decode(ArrayList<Boolean> lonset, double floor, double ceiling) {
                double mid = 0;
                for (int i=0; i<lonset.size(); i++) {
                        mid = (floor + ceiling) / 2;
                        if (lonset.get(i))
                                floor = mid;
                        else
                                ceiling = mid;
                }
                return mid;
        }
        
        
        public String encode(double lat, double lon) {
        	ArrayList<Boolean> latbits = getBits(lat, -90, 90);
        	ArrayList<Boolean> lonbits = getBits(lon, -180, 180);
                StringBuilder buffer = new StringBuilder();
                for (int i = 0; i < numbits; i++) {
                        buffer.append( (lonbits.get(i))?'1':'0');
                        buffer.append( (latbits.get(i))?'1':'0');
                }
                return base32(Long.parseLong(buffer.toString(), 2));
        }

        private ArrayList<Boolean> getBits(double lat, double floor, double ceiling) {
        	ArrayList<Boolean> buffer = new ArrayList<Boolean>(64);
        	for (int i = 0; i < 64; i++) {
        		buffer.add(i,false);
			}
                for (int i = 0; i < numbits; i++) {
                        double mid = (floor + ceiling) / 2;
                        if (lat >= mid) {
                        	buffer.set(i,true);
                                floor = mid;
                        } else {
                                ceiling = mid;
                        }
                }
                return buffer;
        }

        public static String base32(long i) {
                char[] buf = new char[54];
                int charPos = 53;
                boolean negative = (i < 0);
                if (!negative)
                        i = -i;
                while (i <= -32) {
                        buf[charPos--] = digits[(int) (-(i % 32))];
                        i /= 32;
                }
                buf[charPos] = digits[(int) (-i)];

                if (negative)
                        buf[--charPos] = '-';
                return new String(buf, charPos, (54 - charPos));
        }
        

}
