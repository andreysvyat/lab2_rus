/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Record.java to edit this template
 */

package com.example.clinic.doctor.dto;

/**
 *
 * @author thisaster
 */

 public record DoctorDto(
     Long id,
     String name,
     String speciality
 ) {}
