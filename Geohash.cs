using System;
using System.Collections.Generic;
using System.Collections.ObjectModel;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace Rayzit.ViewModels
{

    public class Geohash
    {

        private static int numbits = 5 * 5;
        static char[] digits = { '0', '1', '2', '3', '4', '5', '6', '7', '8',
                        '9', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'j', 'k', 'm', 'n', 'p',
                        'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z' };

        static Dictionary<char, int> lookup = new Dictionary<char, int>();
        static Geohash()
        {
            int i = 0;
            foreach (char c in digits)
                lookup[c] = i++;
        }

        public static double[] decode(string geohash)
        {
            string buffer = string.Empty;
            foreach (char c in geohash.ToCharArray())
            {

                int i = lookup[c] + 32;
                buffer += Convert.ToString(i, 2).Substring(1);
            }

            ObservableCollection<bool> lonset = new ObservableCollection<bool>();
            ObservableCollection<bool> latset = new ObservableCollection<bool>();

            for (int i = 0; i < 64; i++)
            {
                lonset.Add(false);
                latset.Add(false);
            }

            //even bits
            int j = 0;
            for (int i = 0; i < numbits * 2; i += 2)
            {
                bool isSet = false;
                if (i < buffer.Length)
                    isSet = buffer.ToCharArray()[i] == '1';
                lonset.Insert(j++, isSet);
            }

            //odd bits
            j = 0;
            for (int i = 1; i < numbits * 2; i += 2)
            {
                bool isSet = false;
                if (i < buffer.Length)
                    isSet = buffer.ToCharArray()[i] == '1';
                latset.Insert(j++, isSet);
            }

            double lon = decode(lonset, -180, 180);
            double lat = decode(latset, -90, 90);

            return new double[] { lat, lon };
        }

        private static double decode(ObservableCollection<bool> lonset, double floor, double ceiling)
        {
            double mid = 0;
            for (int i = 0; i < lonset.Count; i++)
            {
                mid = (floor + ceiling) / 2;
                if (lonset[i])
                    floor = mid;
                else
                    ceiling = mid;
            }
            return mid;
        }


        public static String encode(double lat, double lon)
        {
            ObservableCollection<bool> latbits = getBits(lat, -90, 90);
            ObservableCollection<bool> lonbits = getBits(lon, -180, 180);
            string buffer = string.Empty;
            for (int i = 0; i < numbits; i++)
            {
                buffer += ((lonbits[i]) ? '1' : '0');
                buffer += ((latbits[i]) ? '1' : '0');
            }
            return base32(Convert.ToInt64(buffer, 2));
        }

        private static ObservableCollection<bool> getBits(double lat, double floor, double ceiling)
        {
            ObservableCollection<bool> buffer = new ObservableCollection<bool>();
            for (int i = 0; i < 64; i++)
            {
                buffer.Add(false);
            }
            for (int i = 0; i < numbits; i++)
            {
                double mid = (floor + ceiling) / 2;
                if (lat >= mid)
                {
                    buffer.Insert(i, true);
                    floor = mid;
                }
                else
                {
                    ceiling = mid;
                }
            }
            return buffer;
        }

        public static String base32(long i)
        {
            char[] buf = new char[54];
            int charPos = 53;
            bool negative = (i < 0);
            if (!negative)
                i = -i;
            while (i <= -32)
            {
                buf[charPos--] = digits[(int)(-(i % 32))];
                i /= 32;
            }
            buf[charPos] = digits[(int)(-i)];

            if (negative)
                buf[--charPos] = '-';
            return new String(buf, charPos, (54 - charPos));
        }


    }

}
