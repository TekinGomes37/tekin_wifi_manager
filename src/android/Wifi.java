package com.tekin.cordova.wifi;

import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.CallbackContext;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * This class echoes a string called from JavaScript.
 */
public class Wifi extends CordovaPlugin {

  private boolean permissionStateWifi = false;

  private Gps gps;
  private WifiManager2 wifi;
  private boolean ready = false;

  @Override
  public boolean execute(String action, JSONArray args, CallbackContext callbackContext) throws JSONException {
    this.prepare();
    switch (action) {
      case "toggleWifi":
        this.toggleWifi(args, callbackContext);
        return true;
      case "getListeWifi":
        this.listWifi(callbackContext);
        return true;
      case "connect":
        this.connect(callbackContext, args);
        return true;
      case "gpsAndWifiState":
        this.gpsAndWifiState(callbackContext);
        return true;
      case "hasPermissions":
        this.hasPermissions(callbackContext);
        return true;
      case "requestPermission":
        this.requestPermission(callbackContext);
        return true;
    }
    return false;
  }

  private void prepare () {
    if (!this.ready) {
      this.gps = new Gps(this.cordova.getContext());
      this.wifi = new WifiManager2(this.cordova.getContext());
      this.ready = true;
    }
  }

  /**
   *Permet de activer/desactiver le wifi du device
   *
   * @param args true pour allume | false pour arreter
   * @param callbackContext promise pour le js, true = on | false = off
   * @throws JSONException error
   */
  private void toggleWifi(JSONArray args, CallbackContext callbackContext) throws JSONException {
    boolean turnWifi = args.getBoolean(0);
    boolean res = this.wifi.toggleWifi(turnWifi);
    JSONObject response = new JSONObject();
    response.put("toggleWifi", res);
    callbackContext.success(response);
  }

  private void gpsAndWifiState (CallbackContext ctx) throws JSONException {
    JSONObject response = new JSONObject();

    boolean stateWifi = this.wifi.getWifiState();
    boolean stateGps = this.gps.getGpsState();

    response.put("wifi", stateWifi);
    response.put("gps", stateGps);

    ctx.success(response);
  }

  /**
   * Permet de verifier si l'app possède les permissions nécessaires pour controller le wifi
   * @param callbackContext callback de la func js
   * @throws JSONException error
   */
  private void hasPermissions (CallbackContext callbackContext) throws JSONException {
     JSONObject response = new JSONObject();

     boolean permGps = this.gps.hasPermGps();
     boolean permWifi = this.wifi.hasPerm();

     response.put("location", permGps);
     response.put("wifi", permWifi);

     callbackContext.success(response);
  }

  /**
   * Demande a la classe GPS de demander la permission a l'utilisateur
   * d'utiliser le wifi et le gps
   * @param ctx callback de la func js
   * @throws JSONException error
   */
  private void requestPermission (CallbackContext ctx) throws JSONException {
    JSONObject response = new JSONObject();
    this.gps.requestPermission(this);
    response.put("requestPermisison", true);
    ctx.success(response);
  }

  /**
   * Permet de lister la listes des reseaux wifi visibles par le device=
   * @param callbackContext context de retour d'appel de function
   */
  private void listWifi(CallbackContext callbackContext) throws JSONException {
    this.wifi.scanWifi(callbackContext);
  }


  /**
   * Permet de se connecter avec un reseau wifi
   * @param callbackContext callback de la func js
   * @param args les information du wifi a se connecter
   * @throws JSONException error
   */
  public void connect(CallbackContext callbackContext, JSONArray args) throws JSONException {
    JSONObject wifi = (JSONObject) args.get(0);
    JSONObject response = new JSONObject();
    this.cordova.getThreadPool().execute(() -> {
      try {
        int res = this.wifi.connectToWifi(wifi);
        if (res == 1) {
          response.put("connect", true);
        } else if (res == 2){
          response.put("error_code", 2);
          response.put("message", "Error add network");
        }
        callbackContext.success(response);
      } catch (JSONException e) {
        e.printStackTrace();
        callbackContext.error(3);
      }
    });
  }

}
