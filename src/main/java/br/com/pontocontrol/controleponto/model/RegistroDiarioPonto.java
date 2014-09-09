/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package br.com.pontocontrol.controleponto.model;

import br.com.pontocontrol.controleponto.SessaoManager;
import br.com.pontocontrol.controleponto.util.TimeUtils;
import java.text.DecimalFormat;
import java.time.LocalTime;
import java.util.Calendar;

/**
 *
 * @author Casa
 */
public class RegistroDiarioPonto {

    private int dia;
    
    private LocalTime entrada;
    private LocalTime almoco;
    private LocalTime retorno;
    private LocalTime saida;
        
    public void registrarProximoAgora() {
        registrarProximo(LocalTime.now());
    }
    
    public void registrarProximo(LocalTime time) {
        if(getEntrada() == null) {
            setEntrada(time);
        } else if(getAlmoco() == null) {
            setAlmoco(time);
        } else if(getRetorno() == null) {
            setRetorno(time);
        } else {
            setSaida(time);
        }
    }
    
    public boolean contabilizarRegistroDiario() {
        return isRegistroDiarioCompleto() || Calendar.getInstance().get(Calendar.DAY_OF_MONTH) != dia;
    }
    
    public boolean isRegistroDiarioCompleto() {
        return getEntrada() != null && getAlmoco() != null && getRetorno() != null && getSaida() != null;
    }
    
    public void registrarProximo(String timeString) {
        registrarProximo(TimeUtils.toLocalTime(timeString));
    }
    
    public void registrarEntrada(String timeString) {
        setEntrada(TimeUtils.toLocalTime(timeString));
    }
    
    public void registrarAlmoco(String timeString) {
        setAlmoco(TimeUtils.toLocalTime(timeString));
    }
    
    public void registrarRetorno(String timeString) {
        setRetorno(TimeUtils.toLocalTime(timeString));
    }
    
    public void registrarSaida(String timeString) {
        setSaida(TimeUtils.toLocalTime(timeString));
    }
    
    public LocalTime calcularTotalAlmoco() {
        if(getAlmoco() != null && getRetorno() != null) {
            return TimeUtils.subtrairLocalTimes(getRetorno(), getAlmoco());
        }
        return null;
    }
    
    public Double calcularTotalAlmocoAsNumber() {
        final LocalTime totalAlmoco = calcularTotalAlmoco();
        if(totalAlmoco != null) {
            return TimeUtils.toNumberLocalTime(totalAlmoco, getOffset());
        } else {
            return 0d;
        }
    }

    public LocalTime calcularTotalExpedienteSemAlmoco() {
        if(getSaida() != null && getEntrada() != null) {
            return TimeUtils.subtrairLocalTimes(getSaida(), getEntrada());
        } else {
            return null;
        }
    }
    
    public LocalTime calcularTotalExpediente() {
        final LocalTime totalExp = calcularTotalExpedienteSemAlmoco();
        final LocalTime totalAlmoco = calcularTotalAlmoco();
        if(totalExp != null && totalAlmoco != null) {
            return TimeUtils.subtrairLocalTimes(totalExp, totalAlmoco);
        } else {
            return null;
        }
    }
    
    public double calcularTotalExpedienteAsNumber() {
        final LocalTime total = calcularTotalExpediente();
        if(total != null) {
            return TimeUtils.toNumberLocalTime(total, getOffset());
        } else {
            return 0d;
        }
    }
    
    public String getTotalExpedienteDecimalFormat() {
        return DecimalFormat.getInstance().format(calcularTotalExpedienteAsNumber());
    }
    
    public Double calcularVariacaoExpediente() {
        return calcularTotalExpedienteAsNumber() - 1;
    }

    public LocalTime getEntrada() {
        return entrada;
    }

    public void setEntrada(LocalTime entrada) {
        this.entrada = entrada;
    }

    public LocalTime getAlmoco() {
        return almoco;
    }

    public void setAlmoco(LocalTime almoco) {
        this.almoco = almoco;
    }

    public LocalTime getRetorno() {
        return retorno;
    }

    public void setRetorno(LocalTime retorno) {
        this.retorno = retorno;
    }

    public LocalTime getSaida() {
        return saida;
    }

    public void setSaida(LocalTime saida) {
        this.saida = saida;
    }

    private long getOffset() {
        ConfiguracoesUsuario usuarioAutenticado = SessaoManager.getInstance().getUsuarioAutenticado();
        if(usuarioAutenticado != null) {
            return usuarioAutenticado.getOffset();
        }
        return TimeUtils.SYSTEM_DEFAULT_OFFSET;
    }

    public int getDia() {
        return dia;
    }

    public void setDia(int dia) {
        this.dia = dia;
    }

}
