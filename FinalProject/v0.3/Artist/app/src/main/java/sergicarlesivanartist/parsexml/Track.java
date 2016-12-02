package sergicarlesivanartist.parsexml;

/**
 * Created by Sergi on 02/12/2016.
 */

import java.util.ArrayList;
import java.util.List;

public class Track {

    private int trackid;
    private String nombre;
    private String artista;
    private String album;
    private String genero;
    private String tipo;
    private int tamaño;
    private int tiempototal;
    private int año;
    private int bpm;
    private String fechamodificada;
    private String fechaañadida;
    private int bitrate;
    private int samplerate;
    private String comentarios;
    private int skipcount;
    private String skipdate;
    private String persistentid;
    private String tracktype;
    private String localizacion;
    private int filefoldercount;
    private int libraryfoldercount;


    public static List<Track> Tracks = new ArrayList<>();

    public Track(int trackid,
                 String nombre,
                 String artista,
                 String album,
                 String genero,
                 String tipo,
                 int tamaño,
                 int tiempototal,
                 int año,
                 int bpm,
                 String fechamodificada,
                 String fechaañadida,
                 int bitrate,
                 int samplerate,
                 String comentarios,
                 int skipcount,
                 String skipdate,
                 String persistentid,
                 String tracktype,
                 String localizacion,
                 int filefoldercount,
                 int libraryfoldercount) {

        this.trackid = trackid;
        this.nombre = nombre;
        this.artista = artista;
        this.album = album;
        this.genero = genero;
        this.tipo = tipo;
        this.tamaño = tamaño;
        this.tiempototal = tiempototal;
        this.año = año;
        this.bpm = bpm;
        this.fechamodificada = fechamodificada;
        this.fechaañadida = fechaañadida;
        this.bitrate = bitrate;
        this.samplerate = samplerate;
        this.comentarios = comentarios;
        this.skipcount = skipcount;
        this.artista = artista;
        this.skipdate = skipdate;
        this.persistentid = persistentid;
        this.tracktype = tracktype;
        this.localizacion = localizacion;
        this.filefoldercount = filefoldercount;
        this.libraryfoldercount = libraryfoldercount;
    }


    public int getTrackid() {
        return trackid;
    }

    public void setTrackid(int trackid) {
        this.trackid = trackid;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getArtista() {
        return artista;
    }

    public void setArtista(String artista) {
        this.artista = artista;
    }

    public String getAlbum() {
        return album;
    }

    public void setAlbum(String album) {
        this.album = album;
    }

    public String getGenero() {
        return genero;
    }

    public void setGenero(String genero) {
        this.genero = genero;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public int getTamaño() {
        return tamaño;
    }

    public void setTamaño(int tamaño) {
        this.tamaño = tamaño;
    }

    public int getTiempototal() {
        return tiempototal;
    }

    public void setTiempototal(int tiempototal) {
        this.tiempototal = tiempototal;
    }

    public int getAño() {
        return año;
    }

    public void setAño(int año) {
        this.año = año;
    }
}
