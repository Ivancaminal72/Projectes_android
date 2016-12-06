package sergicarlesivanartist.parsexml;

/**
 * Created by Sergi on 02/12/2016.
 */

import android.util.Xml;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;


public class parsexml {

    private static final String ns = null;

    private static final String ETIQUETA_trackid = "TrackID";
    private static final String ETIQUETA_nombre = "Name";
    private static final String ETIQUETA_artista = "Artist";
    private static final String ETIQUETA_album ="asr";
    private static final String ETIQUETA_genero = "Genre";
    private static final String ETIQUETA_tipo = "Kind";
    private static final String ETIQUETA_tamaño = "Size";
    private static final String ETIQUETA_tiempototal = "Total Time";
    private static final String ETIQUETA_año = "Year";
    private static final String ETIQUETA_bpm = "asrt";
    private static final String ETIQUETA_fechamodificada = "Date Modified";
    private static final String ETIQUETA_fechaañadida = "Date Added";
    private static final String ETIQUETA_bitrate = "BitRate";
    private static final String ETIQUETA_samplerate = "Sample Rate";
    private static final String ETIQUETA_comentarios = "Comments";
    private static final String ETIQUETA_skipcount = "ttt";
    private static final String ETIQUETA_skipdate = "yyy";
    private static final String ETIQUETA_persistentid = "PersistentId";
    private static final String ETIQUETA_tracktype = "Track Type";
    private static final String ETIQUETA_localizacion = "Location";
    private static final String ETIQUETA_filefoldercount ="File Folder Count";
    private static final String ETIQUETA_libraryfoldercount = "Library Folder Count";



    public List<Track> parsear(InputStream in) throws XmlPullParserException, IOException {
        try {
            XmlPullParser parser = Xml.newPullParser();
            parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, true);
            parser.setInput(in, null);
            parser.nextTag();
            return leerTracks(parser);
        } finally {
            in.close();
        }
    }

    private List<Track> leerTracks(XmlPullParser parser)
            throws XmlPullParserException, IOException {
        List<Track> listaTracks = new ArrayList<Track>();

        parser.require(XmlPullParser.START_TAG, ns, ETIQUETA_trackid);

        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }
            String TrackId = parser.getText();

            if (TrackId.equals(ETIQUETA_trackid)) {
                listaTracks.add(leerTrack(parser));
            } else {
                saltarEtiqueta(parser);
            }
        }
        return listaTracks;
    }



    private Track leerTrack(XmlPullParser parser) throws XmlPullParserException, IOException {
        parser.require(XmlPullParser.START_TAG, ns, ETIQUETA_trackid);

        int trackid = 0;
        String nombre ="";
        String artista = "";
        String album = "";
        String genero = "";
        String tipo = "";
        int tamaño = 0;
        int tiempototal = 0;
        int año = 0;
        int bpm = 0;
        String fechamodificada = "";
        String fechaañadida = "";
        int bitrate = 0;
        int samplerate = 0;
        String comentarios = "";
        int skipcount = 0;
        String skipdate = "";
        String persistentid = "";
        String tracktype = "";
        String localizacion = "";
        int filefoldercount = 0;
        int libraryfoldercount = 0;



        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }

            String name = parser.getText();

            switch (name) {
                case ETIQUETA_trackid:
                    trackid = leertrackid(parser);
                    break;

                case ETIQUETA_nombre:
                    nombre = leernombre(parser);
                    break;

                case ETIQUETA_artista:
                    artista = leerartista(parser);
                    break;

                case ETIQUETA_album:
                    album = leeralbum(parser);
                    break;

                case ETIQUETA_genero:
                    genero = leergenero(parser);
                    break;

                case ETIQUETA_tipo:
                    tipo = leertipo(parser);
                    break;

                case ETIQUETA_tamaño:
                    tamaño = leertamaño(parser);
                    break;

                case ETIQUETA_tiempototal:
                    tiempototal = leertiempototal(parser);
                    break;

                case ETIQUETA_año:
                    año = leeraño(parser);
                    break;

                case ETIQUETA_bpm:
                    bpm = leerbpm(parser);
                    break;

                case ETIQUETA_fechamodificada:
                    fechamodificada = leerfechamodificada(parser);
                    break;

                case ETIQUETA_fechaañadida:
                    fechaañadida = leerfechaañadida(parser);
                    break;

                case ETIQUETA_bitrate:
                    bitrate = leerbitrate(parser);
                    break;

                case ETIQUETA_samplerate:
                    samplerate = leersamplerate(parser);
                    break;

                case ETIQUETA_comentarios:
                    comentarios = leercomentarios(parser);
                    break;

                case ETIQUETA_skipcount:
                    skipcount = leerskipcount(parser);
                    break;

                case ETIQUETA_skipdate:
                    skipdate = leerskipdate(parser);
                    break;

                case ETIQUETA_persistentid:
                    persistentid = leerpersistentid(parser);
                    break;

                case ETIQUETA_tracktype:
                    tracktype = leertracktype(parser);
                    break;

                case ETIQUETA_localizacion:
                    localizacion = leerlocalizacion(parser);
                    break;

                case ETIQUETA_filefoldercount:
                    filefoldercount = leerfilefoldercount(parser);
                    break;

                case ETIQUETA_libraryfoldercount:
                    libraryfoldercount = leerlibraryfoldercount(parser);
                    break;

                default:
                    saltarEtiqueta(parser);
                    break;
            }
        }
        return new Track(trackid,
                nombre,
                artista,
                album,
                genero,
                tipo,
                tamaño,
                tiempototal,
                año,
                bpm,
                fechamodificada,
                fechaañadida,
                bitrate,
                samplerate,
                comentarios,
                skipcount,
                skipdate,
                persistentid,
                tracktype,
                localizacion,
                filefoldercount,
                libraryfoldercount);
    }

    // Obtiene el texto de los atributos
    private String obtenerTexto(XmlPullParser parser) throws IOException, XmlPullParserException {
        String resultado = "";
        if (parser.next() == XmlPullParser.TEXT) {
            resultado = parser.getText();
            parser.nextTag();
        }
        return resultado;
    }



    // Procesa la etiqueta Trackid de los tracks
    private int leertrackid(XmlPullParser parser) throws IOException, XmlPullParserException {
        parser.require(XmlPullParser.START_TAG, ns, ETIQUETA_trackid);
        parser.next();
        int trackid = Integer.parseInt(obtenerTexto(parser));
        parser.require(XmlPullParser.END_TAG, ns, ETIQUETA_trackid);
        return trackid;
    }

    private String leernombre(XmlPullParser parser) throws IOException, XmlPullParserException {
        parser.require(XmlPullParser.START_TAG, ns, ETIQUETA_nombre);
        parser.next();
        String nombre = obtenerTexto(parser);
        parser.require(XmlPullParser.END_TAG, ns, ETIQUETA_nombre);
        return nombre;
    }

    private String leerartista(XmlPullParser parser) throws IOException, XmlPullParserException {
        parser.require(XmlPullParser.START_TAG, ns, ETIQUETA_artista);
        parser.next();
        String artista = obtenerTexto(parser);
        parser.require(XmlPullParser.END_TAG, ns, ETIQUETA_artista);
        return artista;
    }

    private String leeralbum(XmlPullParser parser) throws IOException, XmlPullParserException {
        parser.require(XmlPullParser.START_TAG, ns, ETIQUETA_album);
        parser.next();
        String album = obtenerTexto(parser);
        parser.require(XmlPullParser.END_TAG, ns, ETIQUETA_album);
        return album;
    }

    private String leergenero(XmlPullParser parser) throws IOException, XmlPullParserException {
        parser.require(XmlPullParser.START_TAG, ns, ETIQUETA_genero);
        parser.next();
        String genero = obtenerTexto(parser);
        parser.require(XmlPullParser.END_TAG, ns, ETIQUETA_genero);
        return genero;
    }

    private String leertipo(XmlPullParser parser) throws IOException, XmlPullParserException {
        parser.require(XmlPullParser.START_TAG, ns, ETIQUETA_tipo);
        parser.next();
        String tipo = obtenerTexto(parser);
        parser.require(XmlPullParser.END_TAG, ns, ETIQUETA_tipo);
        return tipo;
    }

    private int leertamaño(XmlPullParser parser) throws IOException, XmlPullParserException {
        parser.require(XmlPullParser.START_TAG, ns, ETIQUETA_tamaño);
        parser.next();
        int tamaño = Integer.parseInt(obtenerTexto(parser));
        parser.require(XmlPullParser.END_TAG, ns, ETIQUETA_tamaño);
        return tamaño;
    }

    private int leertiempototal(XmlPullParser parser) throws IOException, XmlPullParserException {
        parser.require(XmlPullParser.START_TAG, ns, ETIQUETA_tiempototal);
        parser.next();
        int tiempototal = Integer.parseInt(obtenerTexto(parser));
        parser.require(XmlPullParser.END_TAG, ns, ETIQUETA_tiempototal);
        return tiempototal;
    }

    private int leeraño(XmlPullParser parser) throws IOException, XmlPullParserException {
        parser.require(XmlPullParser.START_TAG, ns, ETIQUETA_año);
        parser.next();
        int año = Integer.parseInt(obtenerTexto(parser));
        parser.require(XmlPullParser.END_TAG, ns, ETIQUETA_año);
        return año;
    }

    private int leerbpm(XmlPullParser parser) throws IOException, XmlPullParserException {
        parser.require(XmlPullParser.START_TAG, ns, ETIQUETA_bpm);
        parser.next();
        int bpm = Integer.parseInt(obtenerTexto(parser));
        parser.require(XmlPullParser.END_TAG, ns, ETIQUETA_bpm);
        return bpm;
    }

    private String leerfechamodificada(XmlPullParser parser) throws IOException, XmlPullParserException {
        parser.require(XmlPullParser.START_TAG, ns, ETIQUETA_fechamodificada);
        parser.next();
        String fechamodificada = obtenerTexto(parser);
        parser.require(XmlPullParser.END_TAG, ns, ETIQUETA_fechamodificada);
        return fechamodificada;
    }

    private String leerfechaañadida(XmlPullParser parser) throws IOException, XmlPullParserException {
        parser.require(XmlPullParser.START_TAG, ns, ETIQUETA_fechaañadida);
        parser.next();
        String fechaañadida = obtenerTexto(parser);
        parser.require(XmlPullParser.END_TAG, ns, ETIQUETA_fechaañadida);
        return fechaañadida;
    }

    private int leerbitrate(XmlPullParser parser) throws IOException, XmlPullParserException {
        parser.require(XmlPullParser.START_TAG, ns, ETIQUETA_bitrate);
        parser.next();
        int bitrate = Integer.parseInt(obtenerTexto(parser));
        parser.require(XmlPullParser.END_TAG, ns, ETIQUETA_bitrate);
        return bitrate;
    }

    private int leersamplerate(XmlPullParser parser) throws IOException, XmlPullParserException {
        parser.require(XmlPullParser.START_TAG, ns, ETIQUETA_samplerate);
        parser.next();
        int samplerate = Integer.parseInt(obtenerTexto(parser));
        parser.require(XmlPullParser.END_TAG, ns, ETIQUETA_samplerate);
        return samplerate;
    }

    private String leercomentarios(XmlPullParser parser) throws IOException, XmlPullParserException {
        parser.require(XmlPullParser.START_TAG, ns, ETIQUETA_comentarios);
        parser.next();
        String comentarios = obtenerTexto(parser);
        parser.require(XmlPullParser.END_TAG, ns, ETIQUETA_comentarios);
        return comentarios;
    }

    private int leerskipcount(XmlPullParser parser) throws IOException, XmlPullParserException {
        parser.require(XmlPullParser.START_TAG, ns, ETIQUETA_skipcount);
        parser.next();
        int skipcount = Integer.parseInt(obtenerTexto(parser));
        parser.require(XmlPullParser.END_TAG, ns, ETIQUETA_skipcount);
        return skipcount;
    }

    private String leerskipdate(XmlPullParser parser) throws IOException, XmlPullParserException {
        parser.require(XmlPullParser.START_TAG, ns, ETIQUETA_skipdate);
        parser.next();
        String skipdate = obtenerTexto(parser);
        parser.require(XmlPullParser.END_TAG, ns, ETIQUETA_skipdate);
        return skipdate;
    }

    private String leerpersistentid(XmlPullParser parser) throws IOException, XmlPullParserException {
        parser.require(XmlPullParser.START_TAG, ns, ETIQUETA_persistentid);
        parser.next();
        String persistentid = obtenerTexto(parser);
        parser.require(XmlPullParser.END_TAG, ns, ETIQUETA_persistentid);
        return persistentid;

    }

    private String leertracktype(XmlPullParser parser) throws IOException, XmlPullParserException {
        parser.require(XmlPullParser.START_TAG, ns, ETIQUETA_tracktype);
        parser.next();
        String tracktype = obtenerTexto(parser);
        parser.require(XmlPullParser.END_TAG, ns, ETIQUETA_tracktype);
        return tracktype;

    }

    private String leerlocalizacion(XmlPullParser parser) throws IOException, XmlPullParserException {
        parser.require(XmlPullParser.START_TAG, ns, ETIQUETA_localizacion);
        parser.next();
        String localizacion = obtenerTexto(parser);
        parser.require(XmlPullParser.END_TAG, ns, ETIQUETA_localizacion);
        return localizacion;

    }

    private int leerfilefoldercount(XmlPullParser parser) throws IOException, XmlPullParserException {
        parser.require(XmlPullParser.START_TAG, ns, ETIQUETA_filefoldercount);
        parser.next();
        int filefoldercount = Integer.parseInt(obtenerTexto(parser));
        parser.require(XmlPullParser.END_TAG, ns, ETIQUETA_filefoldercount);
        return filefoldercount;
    }

    private int leerlibraryfoldercount(XmlPullParser parser) throws IOException, XmlPullParserException {
        parser.require(XmlPullParser.START_TAG, ns, ETIQUETA_libraryfoldercount);
        parser.next();
        int libraryfoldercount = Integer.parseInt(obtenerTexto(parser));
        parser.require(XmlPullParser.END_TAG, ns, ETIQUETA_libraryfoldercount);
        return libraryfoldercount;
    }


    private void saltarEtiqueta(XmlPullParser parser) throws XmlPullParserException, IOException {
        if (parser.getEventType() != XmlPullParser.START_TAG) {
            throw new IllegalStateException();
        }
        int depth = 1;
        while (depth != 0) {
            switch (parser.next()) {
                case XmlPullParser.END_TAG:
                    depth--;
                    break;
                case XmlPullParser.START_TAG:
                    depth++;
                    break;
            }
        }
    }


}
