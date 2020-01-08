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
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.simple2secure.api.dto.WidgetDTO;
import com.simple2secure.api.model.Context;
import com.simple2secure.api.model.Widget;
import com.simple2secure.api.model.WidgetProperties;
import com.simple2secure.portal.providers.BaseServiceProvider;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class WidgetUtils extends BaseServiceProvider {

	@Autowired
	DeviceUtils deviceUtils;

	@Autowired
	ReportUtils reportUtils;

	public List<WidgetDTO> getWidgetsByUserAndContextId(String userId, String contextId) {
		List<WidgetDTO> widgetDTOList = new ArrayList<>();
		List<WidgetProperties> properties = widgetPropertiesRepository.getPropertiesByUserIdAndContextId(userId, contextId);
		if (properties != null) {
			for (WidgetProperties property : properties) {
				if (property != null) {
					Widget widget = widgetRepository.find(property.getWidgetId());
					Object value = getValueFromApi(widget.getUrl(), contextId);
					widgetDTOList.add(new WidgetDTO(widget, property, value));
					log.info("Adding new widget {} to the list", widget.getName());
				}
			}
		}
		return widgetDTOList;
	}

	/**
	 * This is the temporary solution, because the old one has been making around 30 request pro widget from the client.
	 * 
	 * @param url
	 * @param contextId
	 * @return
	 */
	public Object getValueFromApi(String url, String contextId) {
		Context context = contextRepository.find(contextId);
		if (context != null) {
			if (url.contains("/devActive")) {
				return deviceUtils.getAllDevicesFromCurrentContext(context, false).size();
			} else if (url.contains("/executedQueries")) {
				return reportUtils.countExecutedQueries(context);
			} else if (url.contains("/lastNotifications")) {
				return notificationRepository.getNotificationsWithPagination(contextId, 0, 3);
			}
		}
		return "";
	}

}
