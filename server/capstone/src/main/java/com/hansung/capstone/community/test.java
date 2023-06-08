package com.hansung.capstone.community;

import com.google.maps.internal.PolylineEncoding;
import com.google.maps.model.LatLng;
import com.hansung.capstone.course.UserCourseServiceImpl;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class test {


    public static class NewLatLng extends LatLng{
        public double lat;

        /** The longitude of this location. */
        public double lng;

        /**
         * Constructs a location with a latitude/longitude pair.
         *
         * @param lat The latitude of this location.
         * @param lng The longitude of this location.
         */
        public NewLatLng(double lat, double lng) {
            this.lat = lat;
            this.lng = lng;
        }

        @Override
        public String toUrlValue() {
            // Enforce Locale to English for double to string conversion
            return String.format(Locale.ENGLISH, "%.6f,%.6f", lat, lng);
        }
    }
    public static void main(String[] args) {
        // TODO Auto-generated method stub

//        String temp = "wiytfA_dbgqFQ?k@nJaHBDrq@{IxA@um@@uQk@i`@A_NKgFKkKmDoq@vRjNTnY[eCjKxAnZzA`CrJlOzOhO~JpFbGlEnQ~LlFtD`BhHj@dWoE`YkIld@oDhUzv@jQlJ`JrK~FvS|@rR|@lCLdPuL~DsErCqBpA[z@MxBUnEGH?pGx@D|CzFa@dKuDx@a@dB{@|@g@WVOd@Mn@Cj@A^?^?h@DTKPGb@GV?zF~@nHnAl@JtS`BhUxBrLNxDF|DwAjBkAlFgE~BaArEsBbC}@lBWlIqB`JGxMxCpCn@tAXpGxAbDb@pAQpGeCFApBo@`CWvCE|DFvLs@pA^j@`@h@^jAp@lDnBfCvAhHhAHxFA|A?r@GnAQ|ICbI?LErNbAnBnCfFt^hMjIlD~P|IhJ|EbCjGj_@vEzH|EpE@rAo^fI|@xGhAlXJxOFpKjAdK?`M?hv@aAj_@g@tPAnRn@zNdAvInB|RJzEpX~A@P?V?xA?zNTZA|Oo@ne@_C|@AfOc@lACt_@yAfGW~Wq@fPy@nIc@|Ki@lWqAnBKbDQtLe@tCOxFQbNYpACfNYrl@gAB{@VoJ~AeHlI}^tP}w@vFyShFoGz@oRwHpAmYnFn@|FlAnCv@rATpLm@`O{AjL_C~O}FxCmDhRkIdNuEnLmAnJIf[nNhPtKhAv@xZjDP}AddFlDj@~AsE`CyGdDLtJ`@lBHn@B`Rg@~Gk@vTuCbOeB`ISjBEtMlFvd@bRf`Brs@bS~HrMfFvf@fZ~OnPeGbG~@nAdCYfE~@dR`UlRxPvNjHvnJB?ze@hLvM`D|HhE|D~E`GdDpa@nj@tVj]bCdDbGfHvMbQxGfIdM|Nt_@tLrIcAlEuAjJuFrU}UbReYlPiUtKwOtLyR|JsNdMyMtJyKtLeHpPgJlDoAlDo@vd@jEvfAzKlNnA`f@bLrWzGxHzEzDnBhCsHb@gEdI@tf@lC|a@zClReBjDyF|DwG~DuGnD_Gb@u@`@kNxBsHjCcHvAqDp@_CdCyFdCuFdI{DdJyAzHiBbCi@z`@}Fz^{Fh[wF`RkDjIiBfNoC`JgCzPgGt_@cNtCkAvE[|^yQ`EkC`]{VfW}QlSwMdEmCjG{DrJcFnBcAfSiMpEyChL_I|k@y`@`G{B`FeBnGeFtEcDlBIhBGN]~CgHhl@mj@lh@ib@pn@a_@|d@wfXsRpWeVxb@qb@v@fAl@z@xQcU`t@gp@ty@qu@`iAedAzYgTe@{Ag@uAjFiE~H}F~MoJr@e@zIyIxI}IdKuJpMqLdRgPlVaVbEhBfCdAbEgIbNsEbF|GtKn@pVzJfD~@fc@rJhDlAxO`GvBtEjHjBnD|@~EnAl~Bbl@bFtAdT`GpPvExMlDtZfJnA^xkAn[~_@zKrJcu@|@_H~B}[d@gO]uL~vA|GlO_]~CsFtf@mfAhKyS`AoBpDkHlXkn@lr@}VwJ_f@vGiCzS}HnE}Ah[qLfUoIle@wQ|J_DzVgJhGyBlQcHn_@sN`IsC`OyF~UeJrFuB~[uLnTgIbnAcd@nB{@bFUrG?fFMdQeDplAef@`KiDv[oLfGeCpGcC`QqFlGi@vLsEhy@k[nIaD|NsF`r@oWnMdp@x{@ujEhVh@zCrGfZlS{H~Am@|H}ChAc@|B{@zGeCdBo@|_@sNk@wCoGs}AcQWwC~PkGzBy@vVcJjCaAk@uCyLsl@eIc`@k@{C}EuWkGoZkAwFxk@gTq@gEWyAWcBYcB_J~C{LzE";
//        System.out.println(Polyline.decode(temp));
        String temp = "wiytfA_dbgqFQ?k@nJaHBDrq@{IxA@um@@uQk@i`@A_NKgFKkKmDoq@vR\\jNTnY[eCjKxAnZzA`CrJlOzOhO~JpFbGlEnQ~LlFtD`BhHj@dWoE`YkIld@oDhUzv@jQlJ`JrK~FvS|@rR|@lCLdPuL~DsErCqBpA[z@MxBUnEGH?pGx@\\D|C\\zFa@dKuDx@a@dB{@|@g@\\WVOd@Mn@Cj@A^?^?h@DTKPGb@GV?zF~@nHnAl@JtS`BhUxBrLNxDF|DwAjBkAlFgE~BaArEsBbC}@lBWlIqB`JGxMxCpCn@tAXpGxAbDb@pAQpGeCFApBo@`CWvCE|DFvLs@pA^j@`@h@^jAp@lDnBfCvAhHhAHxFA|A?r@GnAQ|ICbI?LErNbAnBnCfFt^hMjIlD~P|IhJ|EbCjGj\\_@vEzH|EpE@rAo^fI|@xGhAlXJxOFpKjAdK?`M?hv@aAj_@g@tPAnRn@zNdAvInB|RJzEpX\\~A@P?V?xA?zNTZA|Oo@ne@_C|@AfOc@lACt_@yAfGW~Wq@fPy@nIc@|Ki@lWqAnBKbDQtLe@tCOxFQbNYpACfNYrl@gAB{@VoJ~AeHlI}^tP}w@vFyShFoGz@oR\\wHpAmYnFn@|FlAnCv@rATpLm@`O{AjL_C~O}FxCmDhRkIdNuEnLmAnJIf[nNhPtKhAv@xZjDP}Ad\\dFlDj@~AsE`CyGdDLtJ`@lBHn@B`Rg@~Gk@vTuCbOeB`ISjBEtMlFvd@bRf`Brs@bS~HrMfFvf@fZ~OnPeGbG~@nAdCYfE~@dR`UlRxPvNjHv\\nJB?ze@hLvM`D|HhE|D~E`GdDpa@nj@tVj]bCdDbGfHvMbQxGfIdM|Nt_@tLrIcAlEuAjJuFrU}UbReYlPiUtKwOtLyR|JsNdMyMtJyKtLeHpPgJlDoAlDo@vd@jEvfAzKlNnA`f@bLrWzGxHzEzDnBhCsHb@gEdI@tf@lC|a@zClReBjDyF|DwG~DuGnD_Gb@u@`@kNxBsHjCcHvAqDp@_CdCyFdCuFdI{DdJyAzHiBbCi@z`@}Fz^{Fh[wF`RkDjIiBfNoC`JgCzPgGt_@cNtCkAvE[|^yQ`EkC`]{VfW}QlSwMdEmCjG{DrJcFnBcAfSiMpEyChL_I|k@y`@`G{B`FeBnGeFtEcDlBIhBGN]~CgHhl@mj@lh@ib@pn@a_@|d@w\\fXsRpWeVxb@qb@v@fAl@z@xQcU`t@gp@ty@qu@`iAedAzYgTe@{Ag@uAjFiE~H}F~MoJr@e@zIyIxI}IdKuJpMqLdRgPlVaVbEhBfCdAbEgIbNsEbF|GtKn@pVzJfD~@fc@rJhDlAxO`GvBtEjHjBnD|@~EnAl~Bbl@bFtAdT`GpPvExMlDtZfJnA^xkAn[~_@zKrJcu@|@_H~B}[d@gO]uL~vA|GlO_]~CsFtf@mfAhKyS`AoBpDkHlXkn@lr@}VwJ_f@vGiCzS}HnE}Ah[qLfUoIle@wQ|J_DzVgJhGyBlQcHn_@sN`IsC`OyF~UeJrFuB~[uLnTgIbnAcd@nB{@bFUrG?fFMdQeDplAef@`KiDv[oLfGeCpGcC`QqFlGi@vLsEhy@k[nIaD|NsF`r@oWnMdp@x{@u\\jEhVh@zCrGfZlS{H~Am@|H}ChAc@|B{@zGeCdBo@|_@sNk@wCoGs\\}AcQWwC~PkGzBy@vVcJjCaAk@uCyLsl@eIc`@k@{C}EuWkGoZkAwFxk@gTq@gEWyAWcBYcB_J~C{LzE";
        System.out.println(temp);
        String newTemp = temp.replaceAll("\\\\","");
        System.out.println(newTemp);
        String tmp = temp.replaceAll(File.separator,"");
        System.out.println(tmp);
        System.out.println(Polyline.decode(newTemp));
    }

    public static String convertCoordinatesToPolyline(List<List<Double>> req){
        StringBuffer sb = new StringBuffer();
        List<LatLng> latLngList = new ArrayList<>();
        for(List<Double> coordinates : req){
            Double lat = coordinates.get(0);
            Double lng = coordinates.get(1);
            LatLng latLng = new LatLng(lat, lng);
            latLngList.add(latLng);
        }
        sb.append(UserCourseServiceImpl.Polyline.encode(latLngList));
        return sb.toString();
    }

    public static List<List<Double>> convertPolylineToCoordinates(String req){
        List<List<Double>> coordinates = new ArrayList<>();
        List<LatLng> latLngList = Polyline.decode(req);
        for(LatLng latlng : latLngList){
            List<Double> coordinate = new ArrayList<>();
            Long lat = (long) (latlng.lat * 1e6);
            Math.round(lat);
            coordinates.add(coordinate);
        }
        return coordinates;
    }

    public static class Polyline extends PolylineEncoding{
        public static List<LatLng> decode(final String encodedPath) {

            int len = encodedPath.length();

            final List<LatLng> path = new ArrayList<>(len / 2);
            int index = 0;
            int lat = 0;
            int lng = 0;

            while (index < len) {
                int result = 1;
                int shift = 0;
                int b;
                do {
                    b = encodedPath.charAt(index++) - 63 - 1;
                    result += b << shift;
                    shift += 5;
                } while (b >= 0x1f);
                lat += (result & 1) != 0 ? ~(result >> 1) : (result >> 1);

                result = 1;
                shift = 0;
                do {
                    b = encodedPath.charAt(index++) - 63 - 1;
                    result += b << shift;
                    shift += 5;
                } while (b >= 0x1f);
                lng += (result & 1) != 0 ? ~(result >> 1) : (result >> 1);

                path.add(new NewLatLng(lat * 1e-6, lng * 1e-6));
            }

            return path;
        }
        public static String encode(final List<LatLng> path) {
            long lastLat = 0;
            long lastLng = 0;

            final StringBuilder result = new StringBuilder();

            for (final LatLng point : path) {
                long lat = Math.round(point.lat * 1e6);
                long lng = Math.round(point.lng * 1e6);

                long dLat = lat - lastLat;
                long dLng = lng - lastLng;

                encode(dLat, result);
                encode(dLng, result);

                lastLat = lat;
                lastLng = lng;
            }
            return result.toString();
        }

        private static void encode(long v, StringBuilder result) {
            v = v < 0 ? ~(v << 1) : v << 1;
            while (v >= 0x20) {
                result.append(Character.toChars((int) ((0x20 | (v & 0x1f)) + 63)));
                v >>= 5;
            }
            result.append(Character.toChars((int) (v + 63)));
        }
    }

}
