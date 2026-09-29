package com.example.lcsr_inventory.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.util.Log;

import com.cloudinary.android.MediaManager;
import com.cloudinary.android.callback.ErrorInfo;
import com.cloudinary.android.callback.UploadCallback;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class ImageUtils {

    private static boolean isInitialized = false;

    public interface OnImageUploadListener {
        void onSuccess(String imageUrl);
        void onError(String error);
    }

    public static void initCloudinary(Context context) {
        if (!isInitialized) {
            try {
                Map<String, String> config = new HashMap<>();
                config.put("cloud_name", "xkpvknor");
                config.put("api_key", "544925471877356");
                config.put("api_secret", "WnGiTtEzKMeSEm2kSYETyNlUCoM");
                // Importante: init se llama con el ApplicationContext para evitar Memory Leaks
                MediaManager.init(context.getApplicationContext(), config);
                isInitialized = true;
            } catch (Exception e) {
                // Si ya fue inicializado por otra parte del código, esto evita que crashee
                isInitialized = true; 
            }
        }
    }

    // Comprime la imagen y la sube a Cloudinary
    public static void subirImagenACloudinary(Context context, Uri uriImagenOriginal, OnImageUploadListener listener) {
        initCloudinary(context);

        try {
            // 1. Leer la imagen original desde la URI
            InputStream imageStream = context.getContentResolver().openInputStream(uriImagenOriginal);
            Bitmap bitmapOriginal = BitmapFactory.decodeStream(imageStream);
            
            if(bitmapOriginal == null) {
                listener.onError("No se pudo leer la imagen");
                return;
            }

            // 2. Resolver la rotación de la cámara (EXIF)
            Bitmap bitmapRotado = arreglarRotacion(context, uriImagenOriginal, bitmapOriginal);

            // 3. Redimensionar para ahorrar datos y tiempo de subida
            Bitmap bitmapRedimensionado = redimensionarBitmap(bitmapRotado, 800);

            // 4. Crear un archivo temporal (Solo vivirá unos segundos mientras se sube)
            String nombreArchivo = "temp_" + UUID.randomUUID().toString() + ".jpg";
            File directorio = new File(context.getCacheDir(), "productos_img_temp");
            if (!directorio.exists()) {
                directorio.mkdirs(); 
            }
            File archivoTemporal = new File(directorio, nombreArchivo);

            // 5. Escribir el bitmap comprimido en el archivo temporal
            FileOutputStream outStream = new FileOutputStream(archivoTemporal);
            bitmapRedimensionado.compress(Bitmap.CompressFormat.JPEG, 70, outStream);
            outStream.flush();
            outStream.close();

            // 5. Enviar el archivo a Cloudinary
            MediaManager.get().upload(archivoTemporal.getAbsolutePath())
                    .callback(new UploadCallback() {
                        @Override
                        public void onStart(String requestId) {}

                        @Override
                        public void onProgress(String requestId, long bytes, long totalBytes) {}

                        @Override
                        public void onSuccess(String requestId, Map resultData) {
                            String url = (String) resultData.get("secure_url");
                            String publicId = (String) resultData.get("public_id"); // Obtenemos el ID único de la foto
                            archivoTemporal.delete();
                            
                            // Devolvemos tanto la URL como el publicId (separados por un delimitador para parsear fácil)
                            listener.onSuccess(url + "|||" + publicId);
                        }

                        @Override
                        public void onError(String requestId, ErrorInfo error) {
                            archivoTemporal.delete();
                            listener.onError(error.getDescription());
                        }

                        @Override
                        public void onReschedule(String requestId, ErrorInfo error) {}
                    }).dispatch();

        } catch (Exception e) {
            Log.e("ImageUtils", "Error al procesar imagen: ", e);
            listener.onError(e.getMessage());
        }
    }

    public static void borrarImagenDeCloudinary(Context context, String publicId) {
        if (publicId == null || publicId.isEmpty()) return;
        initCloudinary(context);
        
        try {
            // Nota: Para borrar desde Android de forma segura, se recomienda usar el backend (ej. Firebase Functions)
            // ya que el borrado directo requiere firmas seguras. Sin embargo, para este proyecto,
            // podemos usar el método de destrucción si hemos configurado la política correcta en Cloudinary,
            // o de lo contrario, por seguridad del SDK de Android, simplemente se sobreescribe o invalida.
            MediaManager.get().getCloudinary().uploader().destroy(publicId, com.cloudinary.utils.ObjectUtils.emptyMap());
        } catch (Exception e) {
            Log.e("ImageUtils", "Error borrando imagen antigua: ", e);
        }
    }

    private static Bitmap redimensionarBitmap(Bitmap imagen, int maxSize) {
        int width = imagen.getWidth();
        int height = imagen.getHeight();

        float bitmapRatio = (float) width / (float) height;
        if (bitmapRatio > 1) {
            width = maxSize;
            height = (int) (width / bitmapRatio);
        } else {
            height = maxSize;
            width = (int) (height * bitmapRatio);
        }
        return Bitmap.createScaledBitmap(imagen, width, height, true);
    }

    // Método para arreglar el problema de que las fotos tomadas en vertical salgan acostadas
    private static Bitmap arreglarRotacion(Context context, Uri uri, Bitmap bitmap) {
        try {
            InputStream input = context.getContentResolver().openInputStream(uri);
            if (input == null) return bitmap;
            
            androidx.exifinterface.media.ExifInterface exif = new androidx.exifinterface.media.ExifInterface(input);
            int orientacion = exif.getAttributeInt(androidx.exifinterface.media.ExifInterface.TAG_ORIENTATION, androidx.exifinterface.media.ExifInterface.ORIENTATION_NORMAL);
            
            android.graphics.Matrix matrix = new android.graphics.Matrix();
            switch (orientacion) {
                case androidx.exifinterface.media.ExifInterface.ORIENTATION_ROTATE_90:
                    matrix.postRotate(90);
                    break;
                case androidx.exifinterface.media.ExifInterface.ORIENTATION_ROTATE_180:
                    matrix.postRotate(180);
                    break;
                case androidx.exifinterface.media.ExifInterface.ORIENTATION_ROTATE_270:
                    matrix.postRotate(270);
                    break;
                default:
                    return bitmap;
            }
            
            return Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
        } catch (Exception e) {
            Log.e("ImageUtils", "Error arreglando rotacion", e);
            return bitmap;
        }
    }
}
