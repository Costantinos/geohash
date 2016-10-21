var numbits=5*5;
 var digits=['0', '1', '2', '3', '4', '5', '6', '7', '8','9', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'j', 'k', 'm', 'n', 'p','q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z'];
   var i = 0;
   var lookup={};
      
   for (i=0,len=digits.length;i<len;++i)
    lookup[digits[i]]= i++;     
  
  function base32(i) {  
      
      var buf=new Array(65);
      var charPos = 64;
      var negative = (i < 0);
      var str=""; 
      
      if (!negative)
             i = -i;
      
      while (i <= -32) {
          buf[charPos--] = digits[parseInt(-(i % 32.0))];
          str=digits[parseInt(-(i % 32.0))]+str; 
          i /= 32;
      }
      
      buf[charPos] = digits[parseInt(-i)];
      str=digits[parseInt(-i)]+str;
      
      if (negative){
           buf[--charPos] = '-';
           str='-'+str;
      }
     
      return str;
  }
  
  function getBits(lat,floor,ceiling) {
    var buffer = new Array(64);
    for (i = 0; i < 64; i++) {
     buffer[i]=false;
    }
    
    for (i = 0; i < numbits; i++) {
       var mid = (floor + ceiling) / 2;
       if (lat >= mid) {
             buffer[i]=true;
             floor = mid;
       } else {
             ceiling = mid;
     }
    }
    return buffer;
  }
  
   function encode(lat, lon) {
      var latbits = getBits(lat, -90, 90);
      var lonbits = getBits(lon, -180, 180);
      var buffer = "";
 
      for (i = 0; i < numbits; i++) {
          if(lonbits[i])
              buffer+="1";
          else
              buffer+="0";
          
           if(latbits[i])
              buffer+="1";
          else
              buffer+="0";
      }
     return base32(parseInt(buffer,2));
   }

function decode(geohash) {
    var buffer = "";
    geohash.split('').forEach(function(c){
        
        var i = parseInt(lookup[c]) + 32;
        buffer+=i.toString(2).substring(1);
    });

   var lonset = [];
   var latset = [];

    for (var i = 0; i < 64; i++) {
        lonset[i]=false;
        latset[i]=false;
    }

    var j = 0;
    for (var i = 0; i < numbits * 2; i += 2) {
        var isSet = false;
        if (i < buffer.length){
            isSet = (buffer.charAt(i) == '1');
        }
        lonset[j++]=isSet;
    }

    j = 0;
    for (var i = 1; i < numbits * 2; i += 2) {
        var isSet = false;
        if (i < buffer.length){
            isSet = (buffer.charAt(i) == '1');
        }
        latset[j++]=isSet;
    }

    var lon = decodeIn(lonset, -180, 180);
    var lat = decodeIn(latset, -90, 90);

    return [lat,lon];
}

function decodeIn(lonset,floor,ceiling) {
    var mid = 0;
    for (var i = 0; i < lonset.length; i++) {
        mid = (floor + ceiling) / 2;
        if (lonset[i])
            floor = mid;
        else
            ceiling = mid;
    }
    return mid;
}   
   
   