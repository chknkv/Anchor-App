package com.chknkv.corenetwork.mock

import anchor_app.core.corenetwork.generated.resources.Res
import io.ktor.http.HttpMethod
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
     * @param path   Относительный путь запроса без ведущего слэша (напр. "user/addictions/5").
     * @param method HTTP-метод запроса.
     * @return Строка с JSON, которую отдаст MockEngine.
     * @throws IllegalStateException Если эндпоинт не зарегистрирован в реестре.
     */
    suspend fun resolve(path: String, method: HttpMethod): String {
        return when (method) {
            // Auth

            // FeatureAssistant
            HttpMethod.Get if path == GET_MOTIVATIONAL_QUOTE_ENDPOINT_PATH ->
                readFile(GET_MOTIVATIONAL_QUOTE_STUB_PATH)

            // FeatureAddiction
            HttpMethod.Get if path == GET_ADDICTION_GROUP_FOR_SELECTION_ENDPOINT_PATH ->
                readFile(GET_ADDICTION_GROUP_FOR_SELECTION_STUB_PATH)

            HttpMethod.Post if path == POST_ADDICTION_SAVE_SELECTED_ENDPOINT_PATH ->
                readFile(SUCCESS_RESPONSE_STUB_PATH)

            HttpMethod.Get if path == GET_CLIENT_ADDICTIONS_ALL_ENDPOINT_PATH ->
                readFile(GET_CLIENT_ADDITIONS_ALL_STUB_PATH)

            HttpMethod.Post if path == POST_CLIENT_ADDICTIONS_CREATE_ENDPOINT_PATH ->
                readFile(SUCCESS_RESPONSE_STUB_PATH)

            HttpMethod.Get if path.startsWith("$GET_CLIENT_ADDICTIONS_DETAILS_ENDPOINT_PATH/") ->
                readFile("$GET_CLIENT_ADDITION_DETAILS_STUB_PATH${path.substringAfterLast('/')}.json")

            HttpMethod.Post if path.startsWith("$POST_CLIENT_ADDICTIONS_INCREMENT/") ->
                readFile(SUCCESS_RESPONSE_STUB_PATH)

            HttpMethod.Patch if path.startsWith("$PATCH_CLIENT_ADDICTIONS_UPDATE_ENDPOINT_PATH/") ->
                readFile(SUCCESS_RESPONSE_STUB_PATH)

            HttpMethod.Delete if path.startsWith("$DELETE_CLIENT_ADDICTIONS_DELETE_ENDPOINT_PATH/") ->
                readFile(SUCCESS_RESPONSE_STUB_PATH)

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

    private const val GET_ADDICTION_GROUP_FOR_SELECTION_ENDPOINT_PATH = "addictions/default-selection-groups"
    private const val POST_ADDICTION_SAVE_SELECTED_ENDPOINT_PATH = "addictions/save-selected"
    private const val GET_CLIENT_ADDICTIONS_ALL_ENDPOINT_PATH = "client/addictions/all"
    private const val POST_CLIENT_ADDICTIONS_CREATE_ENDPOINT_PATH = "client/addictions/create"
    private const val GET_CLIENT_ADDICTIONS_DETAILS_ENDPOINT_PATH = "client/addictions/details"
    private const val POST_CLIENT_ADDICTIONS_INCREMENT = "client/addictions/increment"
    private const val PATCH_CLIENT_ADDICTIONS_UPDATE_ENDPOINT_PATH = "client/addictions/update"
    private const val DELETE_CLIENT_ADDICTIONS_DELETE_ENDPOINT_PATH = "client/addictions/delete"
    private const val GET_MOTIVATIONAL_QUOTE_ENDPOINT_PATH = "assistant/motivational-quote"

    private const val SUCCESS_RESPONSE_STUB_PATH = "success_response.json"
    private const val FAILED_RESPONSE_STUB_PATH = "failed_response.json"

    private const val GET_ADDICTION_GROUP_FOR_SELECTION_STUB_PATH = "selection_groups.json"
    private const val GET_CLIENT_ADDITIONS_ALL_STUB_PATH = "addiction_all_groups.json"
    private const val GET_CLIENT_ADDITIONS_ALL_EMPTY_STUB_PATH = "addiction_all_groups_empty.json"
    private const val GET_CLIENT_ADDITION_DETAILS_STUB_PATH = "addiction_details_"
    private const val GET_MOTIVATIONAL_QUOTE_STUB_PATH = "motivational_quote.json"
}
