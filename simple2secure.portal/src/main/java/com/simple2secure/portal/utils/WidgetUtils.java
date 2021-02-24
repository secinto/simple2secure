/**
 *********************************************************************
 *   simple2secure is a cyber risk and information security platform.
 *   Copyright (C) 2019  by secinto GmbH <https://secinto.com>
 *********************************************************************
 *
 *   This program is free software: you can redistribute it and/or modify
 *   it under the terms of the GNU Affero General Public License as
 *   published by the Free Software Foundation, either version 3 of the
 *   License, or (at your option) any later version.
 *
 *   This program is distributed in the hope that it will be useful,
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 *   GNU Affero General Public License for more details.
 *
 *   You should have received a copy of the GNU Affero General Public License
 *   along with this program.  If not, see <https://www.gnu.org/licenses/>.
 *
 *********************************************************************
 */
package com.simple2secure.portal.utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.simple2secure.api.dto.WidgetDTO;
import com.simple2secure.api.model.Context;
import com.simple2secure.api.model.Widget;
import com.simple2secure.api.model.WidgetConfig;
import com.simple2secure.api.model.WidgetProperties;
import com.simple2secure.commons.config.StaticConfigItems;
import com.simple2secure.portal.dao.exceptions.ItemNotFoundRepositoryException;
import com.simple2secure.portal.providers.BaseServiceProvider;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class WidgetUtils extends BaseServiceProvider {

	@Autowired
	DeviceUtils deviceUtils;

	@Autowired
	ReportUtils reportUtils;

	@Autowired
	GroupUtils groupUtils;

	@Autowired
	PortalUtils portalUtils;

	public List<WidgetDTO> getWidgetsByUserAndContextIdAndLocation(String userId, ObjectId contextId, String location)
			throws ItemNotFoundRepositoryException {
		List<WidgetDTO> widgetDTOList = new ArrayList<>();
		List<WidgetProperties> properties = widgetPropertiesRepository.getPropertiesByUserIdAndContextIdAndLocation(userId, contextId,
				location);
		if (properties != null) {
			for (WidgetProperties property : properties) {
				if (property != null) {
					Widget widget = widgetRepository.find(property.getWidgetId());
					if (widget != null) {
						Object value = getWidgetValue(widget.getApi(), contextId);
						widgetDTOList.add(new WidgetDTO(widget, property, value));
						log.info("Adding new widget {} to the list", widget.getName());
					}
				}
			}
		}
		return widgetDTOList;
	}

	/**
	 * This is the temporary solution, because the old one has been making around 30 request pro widget from the client.
	 *
	 * @param api
	 * @param contextId
	 * @return
	 * @throws ItemNotFoundRepositoryException
	 */
	public Object getWidgetValue(String api, ObjectId contextId) throws ItemNotFoundRepositoryException {
		Context context = contextRepository.find(contextId);
		if (context != null) {
			if (api.contains(StaticConfigItems.WIDGET_API_ACTIVE_DEVICES)) {
				return deviceUtils.getAllDevicesFromCurrentContext(context, false).size();
			} else if (api.contains(StaticConfigItems.WIDGET_API_EXEC_QUERIES)) {
				return reportUtils.countExecutedQueries(context);
			} else if (api.contains(StaticConfigItems.WIDGET_API_LAST_NOTIFICATIONS)) {
				return notificationRepository.getNotificationsWithPagination(contextId, 0, 3);
			} else if (api.contains(StaticConfigItems.WIDGET_API_GROUPS)) {
				return groupUtils.getAllGroupsByContextId(context);
			} else if (api.contains(StaticConfigItems.WIDGET_API_GET_CONTEXT_GROUPS_GRAPH)) {
				return groupUtils.getLicenseDownloadsForContext(context, StaticConfigItems.WIDGET_API_GET_CONTEXT_GROUPS_GRAPH);
			} else if (api.contains(StaticConfigItems.WIDGET_API_GET_NUMBER_OF_LICENSE)) {
				return groupUtils.getDataForPieChart(context);
			}
		}
		return "";
	}

	/**
	 * This function returns a widget config which will be used in setting to create a new widget
	 *
	 * @return
	 */
	public WidgetConfig getWidgetConfig() {
		Map<String, String> widgetApis = portalUtils.getWidgetApis();
		Map<String, String> widgetTags = StaticConfigItems.WIDGET_TAGS_DESC;
		List<String> widgetIcons = Arrays.asList(StaticConfigItems.WIDGET_ICONS);
		List<String> widgetColors = Arrays.asList(StaticConfigItems.WIDGET_COLORS);

		WidgetConfig widgetConfig = new WidgetConfig(widgetApis, widgetTags, widgetIcons, widgetColors);
		return widgetConfig;
	}

}
