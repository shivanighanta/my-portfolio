// Copyright 2019 Google LLC
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//     https://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

package com.google.sps;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.List;
import java.util.ArrayList;
import java.util.Arrays;

public final class FindMeetingQuery {
    
    /**
    * Returns all the possible time slots that attendees can attend
    *
    * If one or more time slots exists so that both mandatory and optional attendees can attend,
    * return those time slots. Otherwise, return the time slots that fit just the mandatory attendees.
    */
    public Collection<TimeRange> query(Collection<Event> events, MeetingRequest request) {

      List<String> attendees = new ArrayList<>(request.getAttendees());
      List<String> optionalAttendees = new ArrayList<>(request.getOptionalAttendees());
      int duration = (int) request.getDuration();

      if (attendees.size() == 0) {
        return getTimesPossible(events, request, optionalAttendees, duration);
      }

      if (optionalAttendees.size() == 0) {
          return getTimesPossible(events, request, attendees, duration);
      }

      Collection<TimeRange> timesPossibleMandatoryAttendees = getTimesPossible(events, request, attendees, duration);
      attendees.addAll(optionalAttendees);
      Collection<TimeRange> timesPossibleOptionalAttendees = getTimesPossible(events, request, attendees, duration);

      if (timesPossibleOptionalAttendees.isEmpty()) {
          return timesPossibleMandatoryAttendees;
      }

      return timesPossibleOptionalAttendees;
  }

  /**
  * Returns all the possible time slots that the given attendees can attend with the given duration
  */
  public Collection<TimeRange> getTimesPossible(Collection<Event> events, MeetingRequest request, Collection<String> attendees, int duration) {
      
      // if there are no attendees return whole day
      if (attendees.isEmpty()) {
          return Arrays.asList(TimeRange.WHOLE_DAY);
      }
      // if duration is too long return no options
      if (duration > TimeRange.WHOLE_DAY.duration()) {
          return Arrays.asList();
      }

      // find all the unique conflicting time ranges 
        Set<TimeRange> timesNotPossible = new HashSet<TimeRange>();
        for (Event event : events) {
            for (String attendee : attendees) {
                Set<String> eventAttendees = event.getAttendees();
                TimeRange time = event.getWhen();
                if (eventAttendees.contains(attendee)) {
                    timesNotPossible.add(time);
                }
            }
        } 

        List<TimeRange> timesPossible = new ArrayList<TimeRange>();
        List<TimeRange> timesNotPossibleList = new ArrayList<TimeRange>(timesNotPossible);

        Collections.sort(timesNotPossibleList, TimeRange.ORDER_BY_START);  
        int previousEndTime = TimeRange.START_OF_DAY;

        // find possible times
        for (TimeRange time : timesNotPossibleList) {
            if (previousEndTime < time.start()) {
                TimeRange possibleTimeSlot = TimeRange.fromStartEnd(previousEndTime, time.start(), false);
                if (possibleTimeSlot.duration() >= duration) {
                    timesPossible.add(possibleTimeSlot);
                }
            }
            if (previousEndTime < time.end()) {
                previousEndTime = time.end();
            }
        }

        // add last time slot of the day
        if (previousEndTime < TimeRange.END_OF_DAY) {
            TimeRange lastPossibleTimeSlot = TimeRange.fromStartEnd(previousEndTime, TimeRange.END_OF_DAY, true);
            if (lastPossibleTimeSlot.duration() >= duration) {
                timesPossible.add(lastPossibleTimeSlot);
            }
        }

    Collections.sort(timesPossible, TimeRange.ORDER_BY_START);
    return timesPossible;
  }
}
