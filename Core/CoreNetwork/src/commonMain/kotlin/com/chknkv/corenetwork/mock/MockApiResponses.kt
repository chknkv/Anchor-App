package com.chknkv.corenetwork.mock

import anchor_app.core.corenetwork.generated.resources.Res
import io.ktor.http.HttpMethod
import io.ktor.http.HttpStatusCode
import org.jetbrains.compose.resources.ExperimentalResourceApi

/**
 * Реестр мок-ответов для всех эндпоинтов Anchor API.
 *
 * JSON-стабы хранятся в `composeResources/files/mock/` и читаются через [Res.readBytes].
 * Добавление нового эндпоинта: положите `<name>.json` в папку и зарегистрируйте
 * ветку в [resolve].
 */
@OptIn(ExperimentalResourceApi::class)
internal object MockApiResponses {

    /**
     * Сопоставляет входящий запрос с нужным JSON-файлом и возвращает его содержимое.
     *
     * Порядок `when`-веток имеет значение: более специфичные пути (без wildcard)
     * должны стоять раньше шаблонов с `*`.
     *
     * @param path   Относительный путь запроса без ведущего слэша (напр. "client/addictions/5").
     * @param method HTTP-метод запроса.
     * @return Пара (тело ответа, HTTP-статус), которую отдаст MockEngine.
     * @throws IllegalStateException Если эндпоинт не зарегистрирован в реестре.
     */
    suspend fun resolve(path: String, method: HttpMethod): Pair<String, HttpStatusCode> {
        return when (method) {
            // Auth
            HttpMethod.Post if path == POST_AUTH_OTP_SEND_ENDPOINT_PATH ->
                readFile(POST_AUTH_OTP_SEND_STUB_PATH) to HttpStatusCode.OK

            HttpMethod.Post if path == POST_AUTH_OTP_VERIFY_ENDPOINT_PATH ->
                readFile(POST_AUTH_OTP_VERIFY_STUB_PATH) to HttpStatusCode.OK

            HttpMethod.Post if path == POST_AUTH_OTP_RESEND_ENDPOINT_PATH ->
                "" to HttpStatusCode.NoContent

            // FeatureAssistant
            HttpMethod.Get if path == GET_MOTIVATIONAL_QUOTE_ENDPOINT_PATH ->
                readFile(GET_MOTIVATIONAL_QUOTE_STUB_PATH) to HttpStatusCode.OK

            // FeatureAddiction — онбординг
            HttpMethod.Get if path == GET_ADDICTION_GROUP_FOR_SELECTION_ENDPOINT_PATH ->
                readFile(GET_ADDICTION_GROUP_FOR_SELECTION_STUB_PATH) to HttpStatusCode.OK

            HttpMethod.Post if path == POST_ADDICTION_SAVE_SELECTED_ENDPOINT_PATH ->
                "" to HttpStatusCode.NoContent

            // FeatureAddiction — CRUD
            HttpMethod.Get if path == GET_CLIENT_ADDICTIONS_ALL_ENDPOINT_PATH ->
                readFile(GET_CLIENT_ADDICTIONS_ALL_STUB_PATH) to HttpStatusCode.OK

            HttpMethod.Post if path == POST_CLIENT_ADDICTIONS_CREATE_ENDPOINT_PATH ->
                "" to HttpStatusCode.NoContent

            HttpMethod.Get if path.startsWith("$GET_CLIENT_ADDICTIONS_DETAILS_ENDPOINT_PATH/") ->
                readFile("$GET_CLIENT_ADDITION_DETAILS_STUB_PATH${path.substringAfterLast('/')}.json") to HttpStatusCode.OK

            HttpMethod.Post if path.startsWith("$POST_CLIENT_ADDICTIONS_INCREMENT/") ->
                "" to HttpStatusCode.NoContent

            HttpMethod.Patch if path.startsWith("$PATCH_CLIENT_ADDICTIONS_UPDATE_ENDPOINT_PATH/") ->
                "" to HttpStatusCode.NoContent

            HttpMethod.Delete if path.startsWith("$DELETE_CLIENT_ADDICTIONS_DELETE_ENDPOINT_PATH/") ->
                "" to HttpStatusCode.NoContent

            // Неизвестный эндпоинт
            else -> error("MockApiResponses: no stub for ${method.value} $path")
        }
    }

    /**
     * Читает файл из `composeResources/files/mock/[fileName]` и возвращает его содержимое строкой.
     *
     * @param fileName Имя файла с расширением (напр. `"selection_groups.json"`).
     * @return Содержимое файла в кодировке UTF-8.
     */
    private suspend fun readFile(fileName: String): String = Res.readBytes("files/mock/$fileName").decodeToString()

    private const val POST_AUTH_OTP_SEND_ENDPOINT_PATH = "auth/otp/send"
    private const val POST_AUTH_OTP_VERIFY_ENDPOINT_PATH = "auth/otp/verify"
    private const val POST_AUTH_OTP_RESEND_ENDPOINT_PATH = "auth/otp/resend"
    private const val GET_ADDICTION_GROUP_FOR_SELECTION_ENDPOINT_PATH = "addictions/default-selection-groups"
    private const val POST_ADDICTION_SAVE_SELECTED_ENDPOINT_PATH = "addictions/save-selected"
    private const val GET_CLIENT_ADDICTIONS_ALL_ENDPOINT_PATH = "client/addictions/all"
    private const val POST_CLIENT_ADDICTIONS_CREATE_ENDPOINT_PATH = "client/addictions/create"
    private const val GET_CLIENT_ADDICTIONS_DETAILS_ENDPOINT_PATH = "client/addictions/details"
    private const val POST_CLIENT_ADDICTIONS_INCREMENT = "client/addictions/increment"
    private const val PATCH_CLIENT_ADDICTIONS_UPDATE_ENDPOINT_PATH = "client/addictions/update"
    private const val DELETE_CLIENT_ADDICTIONS_DELETE_ENDPOINT_PATH = "client/addictions/delete"
    private const val GET_MOTIVATIONAL_QUOTE_ENDPOINT_PATH = "assistant/motivational-quote"

    private const val POST_AUTH_OTP_SEND_STUB_PATH = "auth_otp_send.json"
    private const val POST_AUTH_OTP_VERIFY_STUB_PATH = "auth_otp_verify.json"
    private const val GET_ADDICTION_GROUP_FOR_SELECTION_STUB_PATH = "selection_groups.json"
    private const val GET_CLIENT_ADDICTIONS_ALL_STUB_PATH = "addiction_all_groups.json"
    private const val GET_CLIENT_ADDITION_DETAILS_STUB_PATH = "addiction_details_"
    private const val GET_MOTIVATIONAL_QUOTE_STUB_PATH = "motivational_quote.json"
}
