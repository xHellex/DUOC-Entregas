/**
 * Funciones de validación reutilizables.
 * Las usan ContactForm y AdminForm para no duplicar lógica.
 * Cada función devuelve un string con el error, o '' si el valor es válido.
 */

// Expresión regular simple para validar el formato de un correo
const REGEX_EMAIL = /^[^\s@]+@[^\s@]+\.[^\s@]{2,}$/

export const validarRequerido = (valor, nombreCampo) =>
  valor.trim() === '' ? `El campo ${nombreCampo} es obligatorio.` : ''

export const validarLargoMinimo = (valor, minimo, nombreCampo) =>
  valor.trim().length < minimo
    ? `${nombreCampo} debe tener al menos ${minimo} caracteres.`
    : ''

export const validarEmail = (valor) => {
  if (valor.trim() === '') return 'El correo electrónico es obligatorio.'
  return REGEX_EMAIL.test(valor.trim()) ? '' : 'Ingresa un correo válido (ej: nombre@dominio.cl).'
}

export const validarPrecio = (valor, nombreCampo) => {
  if (valor === '') return `El ${nombreCampo} es obligatorio.`
  const numero = Number(valor)
  return Number.isInteger(numero) && numero > 0
    ? ''
    : `El ${nombreCampo} debe ser un número entero mayor a 0.`
}

export const validarUrl = (valor) => {
  if (valor.trim() === '') return 'La URL de la imagen es obligatoria.'
  try {
    const url = new URL(valor.trim())
    return url.protocol === 'http:' || url.protocol === 'https:' ? '' : 'La URL debe comenzar con http:// o https://'
  } catch {
    return 'Ingresa una URL válida (ej: https://sitio.com/imagen.jpg).'
  }
}

/**
 * Devuelve true si el objeto de errores no tiene ningún mensaje.
 * @param {Object<string,string>} errores
 */
export const sinErrores = (errores) => Object.values(errores).every((msg) => msg === '')
